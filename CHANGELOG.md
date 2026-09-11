# Changelog

All notable changes to Parchment, newest first. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

---

## [Unreleased]

The first work on Parchment since 2014. Behaviour of the views is unchanged;
everything around them is new.

### Changed

- **Breaking:** every custom XML attribute now carries a `parchment_`
  prefix: `orientation` is `parchment_orientation`, `cellSpacing` is
  `parchment_cellSpacing`, and so on for all eleven. Custom attribute names
  share one flat namespace across everything the resource merger sees, so
  Parchment's unqualified names collided with any other library declaring
  the same name and the consumer could not build at all; aapt2 stops the
  merge with `duplicate value for resource 'attr/orientation' with config
  ''`. Two declarations survive that merge only when they match exactly, so
  `cellSpacing` as a dimension here and an integer there was already fatal,
  and an attribute carrying `enum` or `flag` children — `orientation`,
  `snapPosition`, `gravity` — collided even when both declarations were
  byte-for-byte identical. Format, meaning, and the enum and flag values
  are unchanged, and the old names are not kept as deprecated aliases:
  2.0 is the breaking window and reading both would double the parsing
  code. The README tables list them all. Substituting your own
  res-auto prefix for `parchment` below — never `android`, whose own
  `orientation` and `gravity` are a different namespace and must not be
  touched — this is the whole migration:

      sed -i -E 's/\bparchment:(orientation|cellSpacing|isCircularScroll|snapToPosition|selectOnSnap|isViewPager|snapPosition|selectWhileScrolling|numberOfViewsPerCell|gravity|ratio)=/parchment:parchment_\1=/g' $(git ls-files '*.xml')

  The `declare-styleable` names (`ListView`, `GridView`, `GridPatternView`)
  are deliberately unchanged: the resource merger unions same-named
  styleables instead of failing them, and each app relinks the library's R
  indices, so a consumer declaring its own `ListView` styleable still
  builds.

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

- With `parchment_snapToPosition` on, a fling ends on the nearest snap position
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
- `parchment_isViewPager="true"` still advances a whole viewport of cells per
  gesture by default, and it is now measured rather than summed. A page runs
  from the cell nearest the snap position to the first cell that does not fit
  the viewport whole, in each direction separately, so cells of different sizes
  page by their own sizes, the landing point is a cell boundary even when the
  gesture starts part-way through a cell, and a padded view counts only what
  fits inside its padding. The room a page has is measured from where the
  anchor cell settles, so `center` and `end` snapping page fewer cells than the
  viewport would hold and no cell is skipped between pages. A cell larger than
  the viewport is the first cell that does not fit, so it is still one page on
  its own (fixed in 1.6.6) without a case of its own. Paging back is the mirror
  of paging forward instead of reusing the forward distance, exactly for cells
  still drawn and extrapolated from the first drawn cell's size for those
  behind them.
- Sample: the demo photo set moved from imgur to the Unsplash CDN, and each
  of the 21 photos now carries its own caption. Eighteen of them read
  "National photo contest", which made paging and snapping hard to follow
  because the caption did not change as the cell did.
- `AbstractAdapterView` invalidates the whole view after adding or removing
  a child instead of the deprecated `invalidate(Rect)`.
- The two ViewPager paging modes are a strategy per mode in `pageinterval/`,
  the way snapping has been one per mode in `snapposition/`:
  `CellCountPageInterval` and `ViewportPageInterval` behind
  `PageIntervalInterface`, picked once from `parchment_viewPagerInterval` by
  `PageIntervalSelector` and held on `LayoutManager`. They had been methods
  on `LayoutManager` with no access modifier so that unit tests in the same
  package could reach them. Every remaining declaration in the library was
  given an explicit `public`, `protected` or `private` at the same time, and
  the unused `Animation.setId` was deleted rather than widened; none of them
  was reachable from outside its package before, so no consumer loses a call.
  Paging behaviour is unchanged.

### Added

- An instrumented harness, `ParchmentViewHarness`, that inflates a view
  from a layout with a real `LayoutInflater`, attaches it to an Activity at
  an exact pixel size, drives real measure and layout passes, dispatches
  real gestures, and hands a test an immutable snapshot of where the
  children landed. All eleven `parchment_` attributes are covered by
  on-device tests built on it, asserting geometry rather than getters, as is
  paging in both modes: a real fling and a real slow drag each page by the
  cells that fit the viewport, or by `parchment_viewPagerInterval` cells, with
  several cells on screen, with cells that do not divide the viewport, with
  cells of unequal size, with a cell taller than the viewport, from a resting
  position part-way through a cell, inside `android:padding`, in both
  orientations, at both ends of the adapter, wrapping with
  `parchment_isCircularScroll`, and on `GridView` and `GridPatternView` where
  a cell is a whole group.
- Spotless (google-java-format, AOSP style) with SPDX license headers on
  every Java file; `javac -Xlint:all -Werror`; Android Lint with warnings
  as errors and no baseline.
- GitHub Actions: build + style + Robolectric tests + emulator-run
  instrumented tests (`android-ci.yml`), security gates (`security.yml`:
  action-pin lint, allowlist expiry, OSV-Scanner, dependency review,
  gitleaks), and CodeQL (`codeql.yml`). Dependabot for Gradle and Actions.
- `parchment_viewPagerInterval`, on all three views, for how far one ViewPager
  gesture pages. It carries an integer plus a named constant the way
  `layout_width` carries a dimension plus `match_parent`: `viewport`, or the
  literal `0`, or leaving the attribute out, keeps the viewport paging
  Parchment has always done, while `N` advances exactly N cells from the cell
  nearest the snap position, which is the carousel issue #26 asked for. Zero
  is both the default and the value an absent attribute already yields, so
  unset and `viewport` are one state and upgrading changes nothing until the
  attribute is set. A negative interval is not a number of cells — it would
  page backwards when the finger went forwards — so it is read as `viewport`
  too. The attribute was named in `LayoutManagerAttributes` since 2014 and
  carried as far as the layout manager, but nothing ever declared it in XML
  or assigned it, so it always arrived as 0.
- An instrumented smoke test that inflates a `ListView` from XML on a real
  framework.
- `CONTRIBUTING.md`, `SECURITY.md`, `CODE_OF_CONDUCT.md`,
  `docs/architecture.md`, issue and PR templates, a pre-commit hook, and
  `scripts/ci_local.sh` mirroring CI.

### Fixed

- A gesture whose first frame is held by the start or the end of the list no
  longer counts the movement it was denied. The layout pass folded the
  clamp's correction back into the distance a gesture has travelled only
  while an animation continued, never on the frame that began it, so a first
  frame carrying a drag the bounds refused left that drag in the running
  total. In ViewPager mode the page that followed was then short by it in one
  direction and long by it in the other: a backward swipe at the first cell
  crept forward by the first frame's drag instead of holding still. Whether a
  gesture's first frame carries a drag at all depends on whether a touch move
  and an animation frame land in the same pass, which made it intermittent
  and invisible to a unit test that drives layout by hand.
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
