# Parchment

[![Android CI](https://github.com/TinkerNorth/parchment/actions/workflows/android-ci.yml/badge.svg)](https://github.com/TinkerNorth/parchment/actions/workflows/android-ci.yml)
[![Security](https://github.com/TinkerNorth/parchment/actions/workflows/security.yml/badge.svg)](https://github.com/TinkerNorth/parchment/actions/workflows/security.yml)
[![CodeQL](https://github.com/TinkerNorth/parchment/actions/workflows/codeql.yml/badge.svg)](https://github.com/TinkerNorth/parchment/actions/workflows/codeql.yml)

Horizontal and vertical **ListView**, **GridView**, **GridPatternView**, and a
**ViewPager** mode for Android, built directly on `AdapterView` with its own
view recycler and layout engine. Snap positioning, circular scrolling, and
repeating grid patterns are single XML attributes rather than add-on helpers.

Parchment predates `RecyclerView`: it was written in 2013 for Google TV and
tablet carousels, when `Gallery` was the only horizontal widget and it kept
every child in memory. The story, and a feature-by-feature comparison with
`RecyclerView`, is in
[the Parchment article on tinkernorth.com](https://tinkernorth.com/parchment-android-adapterview-library/).

## Features

- One attribute flips any view between horizontal and vertical scrolling
- Four snap modes built into the layout engine: `center`, `start`, `end`,
  `onScreen`
- Circular (infinite) scrolling with a single boolean, no adapter tricks
- ViewPager behaviour on the same ListView and the same adapter
- GridView whose rows wrap to the tallest cell
- GridPatternView: declare a repeating pattern of mixed-span cells and let
  the engine tile your data through it
- Classic `Adapter` API, so any `BaseAdapter` you already have works unchanged
- View recycling by item view type, with re-measure only when a view asks
  for it
- D-pad and focus handling inherited from `AdapterView`
- No runtime dependencies

## When to use it

`RecyclerView` is the right default for feeds, chat, and anything with
frequent inserts, removals, or item animations. Parchment is the better fit
when the content is static or slow-changing and you want carousels, pagers,
or magazine-style grids without stitching together a `LayoutManager`, a
`SnapHelper`, and an `Integer.MAX_VALUE` adapter hack.

## Architecture

```
mobi.parchment.widget.adapterview
  ├── AbstractAdapterView         AdapterView subclass: measure/layout, touch, state
  │     ├── listview.ListView       one cell = one view (also the ViewPager mode)
  │     ├── gridview.GridView       one cell = a row/column group that wraps
  │     └── gridpatternview.GridPatternView   one cell = a declared pattern group
  ├── AdapterViewManager          view recycler keyed by adapter view type
  ├── LayoutManager               scrolling, snapping, circular wrap, save/restore
  │     └── ScrollDirectionManager  start/end/size abstracted over orientation
  ├── snapposition.*              Center / Start / End / OnScreen strategies
  └── ChildTouchGestureListener + AdapterAnimator + ScrollAnimator
                                  gestures → fling / snap / page animations
```

The full map, including the recycling contract and the layout pass, is in
[`docs/architecture.md`](docs/architecture.md).

## Requirements

- Android Studio that supports AGP 9.4 (Otter 2025.2 or newer)
- Android SDK 37
- JDK 17+ to run Gradle (the build provisions its own JDK 21 daemon via
  `gradle/gradle-daemon-jvm.properties`)
- Min SDK 21 (Android 5.0) for consumers

## Build and test

```bash
git clone https://github.com/TinkerNorth/parchment.git
cd parchment
./gradlew :library:assembleRelease
```

| Task | Command |
|------|---------|
| Library AAR | `./gradlew :library:assembleRelease` |
| Sample app | `./gradlew :sample:installDebug` |
| Unit tests (Robolectric) | `./gradlew :library:test` |
| Instrumented tests (device or emulator) | `./gradlew :library:connectedDebugAndroidTest` |
| Lint | `./gradlew :library:lintDebug :sample:lintDebug` |
| Format | `./gradlew spotlessApply` |
| Every CI gate locally | `scripts/ci_local.sh` |

## Usage

### XML

```xml
<mobi.parchment.widget.adapterview.listview.ListView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:parchment="http://schemas.android.com/apk/res-auto"
    android:id="@+id/horizontal_list_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parchment:orientation="horizontal"
    parchment:cellSpacing="10dp"
    parchment:snapPosition="center"
    parchment:snapToPosition="true"
    parchment:isCircularScroll="false"
    parchment:isViewPager="false" />
```

### Java

```java
ListView<BaseAdapter> listView = findViewById(R.id.horizontal_list_view);
listView.setAdapter(adapter);
```

`GridPatternView` takes its pattern in code. Each `GridPatternItemDefinition`
is `(left, top, width, height)` in grid cells; every group you add repeats in
sequence across the adapter:

```java
GridPatternView<BaseAdapter> view = findViewById(R.id.parchment_view);
List<GridPatternItemDefinition> hero = new ArrayList<>();
hero.add(new GridPatternItemDefinition(0, 0, 4, 4));
hero.add(new GridPatternItemDefinition(4, 0, 2, 2));
hero.add(new GridPatternItemDefinition(4, 2, 2, 2));
view.addGridPatternGroupDefinition(hero);
view.setAdapter(adapter);
```

The [sample app](sample/src/main/java/mobi/parchment) exercises all four
views.

### XML attributes

All views:

| Attribute | Type | Description |
|-----------|------|-------------|
| `orientation` | `horizontal` or `vertical` | Scroll direction |
| `cellSpacing` | dimension | Space between cells; outer padding comes from `android:padding*` |
| `isCircularScroll` | boolean | Wrap from the last item back to the first |
| `snapToPosition` | boolean | Settle on a cell after a scroll or fling |
| `snapPosition` | `center`, `start`, `end`, `onScreen` | Where a cell settles |
| `selectOnSnap` | boolean | Fire `OnItemSelectedListener` when a snap completes |
| `selectWhileScrolling` | boolean | Fire selection while the content is still moving |
| `isViewPager` | boolean | One cell per gesture, ViewPager style |

GridView:

| Attribute | Type | Description |
|-----------|------|-------------|
| `numberOfViewsPerCell` | integer | Views per row (vertical) or column (horizontal) |
| `gravity` | `left`, `right`, `top`, `bottom` flags | Where shorter views sit inside a wrapping cell |

GridPatternView:

| Attribute | Type | Description |
|-----------|------|-------------|
| `ratio` | float | Aspect ratio of one grid cell |

## Project layout

```
library/
  src/main/java/mobi/parchment/   Library (Java)
  src/main/res/values/attrs.xml   Custom XML attributes
  src/test/                       Robolectric unit tests
  src/androidTest/                Instrumented smoke tests
  consumer-rules.pro              Keep rules applied to consumers' R8 builds
sample/                           Demo app: ListView, GridView, GridPatternView, ViewPager
docs/architecture.md              Layout engine, recycler, and gesture pipeline
config/spotless/                  License header applied by Spotless
gradle/libs.versions.toml         Version catalog
.github/workflows/                CI, security, and CodeQL pipelines
```

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md) and the
[`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md). CI runs build + style
(`android-ci.yml`: Spotless, Android Lint with warnings as errors, Robolectric
unit tests, an emulator run of the instrumented tests) and security gates
(`security.yml`, `codeql.yml`: OSV-Scanner, dependency-review, gitleaks,
action-pin lint, and CodeQL for `java-kotlin`) on every PR.

> Branch protection is unavailable on this repo's current org plan, so
> direct pushes to `main` are blocked by convention only. The CI workflows
> are the de-facto gate.

## Security

Vulnerability disclosure: [`SECURITY.md`](SECURITY.md).

## Releases

Parchment 2.0 is not published yet. The last release, `mobi.parchment:parchment:1.6.9`
on Maven Central, is the 2014 build and targets the pre-Gradle toolchain;
build the AAR from source until 2.0 ships. Notable changes are tracked in
[`CHANGELOG.md`](CHANGELOG.md).

## Acknowledgements

Parchment was written by Emir Hasanbegovic, with Anthony Tarantini as the
other original contributor. Thanks also to everyone who filed issues and
pull requests against the original `EmirWeb/parchment` repository.

## License

Apache-2.0. See [`LICENSE`](LICENSE).
