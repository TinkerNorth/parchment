# Changelog

All notable changes to Parchment, newest first. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

---

## [Unreleased]

The first work on Parchment since 2014. Behaviour of the views is unchanged;
everything around them is new.

### Changed

- Sample: each photo is requested from the Unsplash resizer at the size it
  will be shown at, instead of a fixed 800px wide image for every cell. The
  size is a pair of dimension resources per sample screen, so the existing
  `-port` and `sw###dp` qualifiers pick it the same way they already pick the
  cell sizes themselves; the three that a layout dimension already states
  alias it rather than repeating the number. Requests are in device pixels,
  so density is applied once by `getDimensionPixelSize`.
- Sample: Picasso is built with a memory cache of a third of the heap instead
  of the seventh it sizes itself at. A photo requested at the size of the view
  is several times larger than the fixed 800px one it replaces, and on a
  256MB-heap device only three GridPatternView cells fit in the default cache,
  so scrolling back evicted almost everything: 4 hits against 104 misses over
  a pass through the pattern view and back. A third of the heap, with the
  pattern view's request trimmed, makes that 24 hits against 72.

- With `snapToPosition` on, a fling ends on the nearest snap position
  instead of decelerating to a stop and then starting a separate snap. The
  layout manager computes the adjustment when the fling starts, extrapolating
  with the edge cell's size when the end lies beyond the visible cells, and
  the fling's end point is moved there while its physics stay the same.
- Snap, page, tap-to-snap, and programmatic scrolls use a decelerate curve
  with a duration proportional to the distance (100 ms per inch, stretched
  for the deceleration, capped at 500 ms), the same numbers RecyclerView's
  snap helper uses. The old fixed 500 ms viscous-fluid curve covered 97% of
  the distance in its first half and crawled the last few pixels one at a
  time over the second half, which read as stutter at the end of a scroll.
  Fling physics are unchanged.
- Animation frames (fling, snap, page, drag) run the layout step directly
  from a `postOnAnimation` callback and invalidate, instead of posting a
  `requestLayout()` that re-measured and re-laid out the whole ancestor
  tree every frame. A full layout pass still happens whenever the framework
  asks for one, and a frame that fires while one is pending yields to it.
- **Breaking:** minimum SDK is 21 (was 8) and the library is compiled with
  Java 17 against SDK 37.
- **Breaking:** the library is an AAR built by Gradle. The Maven `apklib`
  packaging, the `pom.xml` files, and the Eclipse project files are gone.
- Custom attributes are declared through the `res-auto` namespace
  (`xmlns:parchment="http://schemas.android.com/apk/res-auto"`).
- Build: Gradle 9.7.1 with a checksummed wrapper, AGP 9.4.0, a version
  catalog, configuration cache, and a JDK 21 daemon toolchain provisioned
  by Gradle.
- Tests: Robolectric 4.16.1, AssertJ 3.27, androidx.test 1.7; the old
  `integration` Maven module lives in `library/src/test` now.
- Sample: targets SDK 37, Material theme, RTL-aware padding, Picasso 2.71828.
- Sample: the demo photo set moved from imgur to the Unsplash CDN, and each
  of the 21 photos now carries its own caption. Eighteen of them read
  "National photo contest", which made paging and snapping hard to follow
  because the caption did not change as the cell did.
- `AbstractAdapterView` invalidates the whole view after adding or removing
  a child instead of the deprecated `invalidate(Rect)`.

### Added

- Spotless (google-java-format, AOSP style) with SPDX license headers on
  every Java file; `javac -Xlint:all -Werror`; Android Lint with warnings
  as errors and no baseline.
- GitHub Actions: build + style + Robolectric tests + emulator-run
  instrumented tests (`android-ci.yml`), security gates (`security.yml`:
  action-pin lint, allowlist expiry, OSV-Scanner, dependency review,
  gitleaks), and CodeQL (`codeql.yml`). Dependabot for Gradle and Actions.
- An instrumented smoke test that inflates a `ListView` from XML on a real
  framework.
- `CONTRIBUTING.md`, `SECURITY.md`, `CODE_OF_CONDUCT.md`,
  `docs/architecture.md`, issue and PR templates, a pre-commit hook, and
  `scripts/ci_local.sh` mirroring CI.

### Fixed

- A view with `snapToPosition` on comes to rest after a gesture instead of
  requesting animation frames forever. A snap asked the content to move to a
  start that the draw limits, which are what stop a scroll, refuse to reach:
  the clamp put the content back, the stop asked for the distance again, got
  the same non-zero answer, and started another snap. Each snap position now
  derives its snapped start, its two draw limits and its snap distance from
  one formula, so the start a snap asks for is exactly the start the clamp
  allows, in both directions. Two ways of reaching the old mismatch:
  - The snap distance was measured against the cell's representative view
    while the draw limits were measured against the cell. Those agree only
    while the representative is exactly as big as its cell, which holds for
    `ListView`, whose cell is the view, and for `GridView`, whose
    representative is the row's tallest view and so is what sets the row's
    size, but not for a `GridPatternView` pattern of more than one row, whose
    representative is one grid row of it. This reached `center` and `end`.
  - `center` built its forward draw limit as `(size + cellSize) / 2` and its
    target as `(size - cellSize) / 2`. Integer division truncates toward
    zero, so the two rounded opposite ways once a cell was larger than the
    viewport, and a cell larger by an odd number of pixels left a permanent
    one-pixel error. This reached all three views.
- A `GridPatternView` no longer adds the view's start padding a second time
  when placing each view inside its cell. The cell start already carries the
  padding, so with `android:paddingTop` set (`paddingLeft` when scrolling
  horizontally) the pattern was drawn that far past where the engine held the
  cell, and a centred cell came to rest off centre by the padding.
- A fling, page, or programmatic scroll that ends hands off to its snap in
  the same frame, after that frame's layout. The snap used to start one frame
  later and was started twice, which left a frame with no motion at the
  handoff. A layout pass while the view is at rest away from a snap position
  still starts the snap, as before.
- Animations no longer call `requestLayout()` from inside the layout pass.
  `AdapterAnimator` asks an `AnimationFrameScheduler` (implemented by the
  view) for the next frame, and the view posts a single coalesced request,
  so starting a snap or fling while laying out no longer triggers the
  framework's "requestLayout() improperly called during layout" second pass.
- `AbstractAdapterView.onMeasure` reports the size from the measure spec
  instead of the raw spec (which leaked the spec mode into
  `getMeasuredState()`), and `onLayout` no longer runs `MeasureSpec.getSize`
  on its layout coordinates, which laid out nothing when the view sat at a
  negative offset inside its parent.
- Snapping (after a drag or fling, on tap, on `setSelection`, and after a
  data set change) now measures against the size inside the view's padding,
  the same size the layout pass uses. With padding set, `center` and `end`
  snaps used to target a point shifted by the padding, so the snap target
  could disagree with where the content is allowed to stop.
- Drag distance is accumulated between layout passes and consumed by the pass
  that applies it. Two move events before one layout used to lose the first
  delta, and an extra layout pass during a drag (an image loading into a
  child, for instance) applied the last delta a second time.
- The post-layout over-scroll check now reuses the draw-limit clamp in both
  directions. `center` and `end` snap positions no longer stop a fling (and
  start a snap) while the first or last cell is merely visible, a frame that
  overshoots either end of the content is pulled back in the same frame
  instead of one frame later, circular scrolling is never "corrected", and
  the first visible cell index is no longer overwritten by the check (which
  broke the scrollbar offset and refilling when scrolling back).
- Restoring saved state into a freshly created view (rotation) kept the
  first visible cell but lost the scroll offset inside it: the first layout
  pass snapped back to the cell start. The restored offset now survives.

### Removed

- Travis CI configuration, the `integration` module, lint baselines, and a
  stray editor backup file.

### Moved

- The repository now lives at `TinkerNorth/parchment`; the old
  `EmirWeb/parchment` URL redirects.

---

## [1.6.6] - 2014-04-21

### Changed

- Revamped sample application.

### Fixed

- `snapToPosition` combined with `snapPosition="start"`.
- ViewPager mode paging continuously when a view is larger than the screen.

## [1.6.5] - 2014-04-15

### Changed

- Cell spacing no longer applies to the outer edges of the view. Use
  `android:padding*` to control how close cells sit to the sides, and
  `android:clipToPadding="false"` to let cells draw over the padding.

## [1.5.7] - 2014-03-28

First tagged release. Horizontal and vertical `ListView`, `GridView`, and
`GridPatternView`, with snap positioning, circular scrolling, and ViewPager
mode. Published to Maven Central as `mobi.parchment:parchment`.
