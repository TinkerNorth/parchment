# Changelog

All notable changes to Parchment, newest first. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

---

## [Unreleased]

The first work on Parchment since 2014. Behaviour of the views is unchanged;
everything around them is new.

### Changed

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
