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
- ViewPager behaviour on the same ListView and the same adapter: one
  completed gesture advances a whole viewport of cells, or exactly
  `parchment_viewPagerInterval` of them
- A divider drawn between adjacent cells, in either orientation and in all
  three views
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
    parchment:parchment_orientation="horizontal"
    parchment:parchment_cellSpacing="10dp"
    parchment:parchment_snapPosition="center"
    parchment:parchment_snapToPosition="true"
    parchment:parchment_isCircularScroll="false"
    parchment:parchment_isViewPager="false"
    parchment:parchment_viewPagerInterval="viewport" />
```

### Dividers

`parchment_divider` paints a drawable or a colour between adjacent cells,
one fewer divider than there are cells on screen: never before the first
cell or after the last. In a `ListView` a cell is one view, so a divider
sits between items. In `GridView` and `GridPatternView` a cell is a whole
group, so a divider separates rows (or columns, scrolling horizontally),
never the items inside a row.

The divider is decoration and takes no space of its own: it is centred in
the `parchment_cellSpacing` gap between the two cells, so the gap is what
you size to make room for it. A divider thicker than the spacing overflows
evenly onto both cells and is painted over them, which is also what makes
one visible when the spacing is zero. Along the breadth it runs from
`android:paddingLeft` to `android:paddingRight` (from `paddingTop` to
`paddingBottom` scrolling horizontally). `android:clipToPadding` decides
whether a cell is clipped at the padding; either way it never reaches the
divider, which is painted inside the padding regardless.

`parchment_dividerSize` gives the thickness along the scroll axis. Leave it
out and the drawable's intrinsic size along that axis is used instead — but
a colour has no intrinsic size, so a colour divider with no
`parchment_dividerSize` paints nothing, the same as the platform `ListView`.
A `parchment_dividerSize` of zero or less paints nothing either; that is not
the same as leaving the attribute out, which is what asks for the intrinsic
size.

The drawable is drawn as it is given: its state is not tracked and an
`AnimationDrawable` will not animate, again as with the platform `ListView`.

```xml
<mobi.parchment.widget.adapterview.listview.ListView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:parchment="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parchment:parchment_cellSpacing="8dp"
    parchment:parchment_divider="#33000000"
    parchment:parchment_dividerSize="1dp" />
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
| `parchment_orientation` | `horizontal` or `vertical` | Scroll direction |
| `parchment_cellSpacing` | dimension | Space between cells; outer padding comes from `android:padding*` |
| `parchment_isCircularScroll` | boolean | Wrap from the last item back to the first |
| `parchment_snapToPosition` | boolean | Settle on a cell after a scroll or fling |
| `parchment_snapPosition` | `center`, `start`, `end`, `onScreen` | Where a cell settles |
| `parchment_selectOnSnap` | boolean | Fire `OnItemSelectedListener` when a snap completes |
| `parchment_selectWhileScrolling` | boolean | Fire selection while the content is still moving |
| `parchment_isViewPager` | boolean | One page per gesture, ViewPager style |
| `parchment_viewPagerInterval` | integer, or `viewport` | How far one ViewPager gesture pages. `viewport` (or `0`, or unset, the default) advances every cell that fits the viewport whole; `N` advances exactly N cells. Values below zero are read as `viewport` |
| `parchment_divider` | drawable or colour | Drawn between adjacent cells, never before the first or after the last |
| `parchment_dividerSize` | dimension | Divider thickness along the scroll axis; without it the drawable's intrinsic size along that axis is used, and a colour has none, so a colour divider with no size paints nothing |

GridView:

| Attribute | Type | Description |
|-----------|------|-------------|
| `parchment_numberOfViewsPerCell` | integer | Views per row (vertical) or column (horizontal) |
| `parchment_gravity` | `left`, `right`, `top`, `bottom` flags | Where shorter views sit inside a wrapping cell |

GridPatternView:

| Attribute | Type | Description |
|-----------|------|-------------|
| `parchment_ratio` | float | Aspect ratio of one grid cell |

### Paging

`parchment_isViewPager="true"` turns a completed gesture into a page, however
far the finger travelled. `parchment_viewPagerInterval` says how far a page is,
and it carries two kinds of value in the way `layout_width` carries a dimension
plus `match_parent`:

```xml
<!-- Magazine spread: advance every cell that fits the viewport whole.
     This is the default, so the attribute can be left out entirely. -->
<mobi.parchment.widget.adapterview.gridpatternview.GridPatternView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parchment:parchment_isViewPager="true"
    parchment:parchment_viewPagerInterval="viewport" />

<!-- Carousel: advance exactly one cell, whatever else is on screen. -->
<mobi.parchment.widget.adapterview.listview.ListView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parchment:parchment_isViewPager="true"
    parchment:parchment_viewPagerInterval="1" />
```

Viewport paging adapts to the screen with no configuration: three cells on a
tablet, one on a phone, and a cell larger than the viewport is one page on its
own. A page is the run of cells that fit the viewport whole from wherever the
nearest cell settles, so a partial cell at the edge is not counted this page
and is the first cell of the next one. The last page is short rather than
running off the end.

An interval of `N` ignores what fits and advances N cells from the cell nearest
the snap position, measured from each cell's own start, so cells of different
sizes still land on a cell boundary. Both modes wrap when
`parchment_isCircularScroll` is on and stop at the adapter's ends when it is
not.

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
