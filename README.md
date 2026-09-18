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
- A divider drawn on every edge internal to the content, in either
  orientation and in all three views, including between the items stacked
  inside one `GridView` row or one `GridPatternView` group
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

## Installation

Parchment is on Maven Central as `mobi.parchment:parchment`, the same
coordinates as the 2014 releases.

```kotlin
// build.gradle.kts
dependencies {
    implementation("mobi.parchment:parchment:2.2.0")
}
```

Or in a version catalog:

```toml
[libraries]
parchment = { group = "mobi.parchment", name = "parchment", version = "2.2.0" }
```

2.0 is a breaking upgrade from 1.6.x: every XML attribute gained a
`parchment_` prefix, and a few `protected` hooks changed shape. The class
names and packages are unchanged. [`CHANGELOG.md`](CHANGELOG.md) lists every
change, with the breaking ones marked.

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
| Unit tests (Robolectric) | `./gradlew :library:test :sample:testDebugUnitTest` |
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

### Sizing

`match_parent` and a fixed size behave as on any view. `wrap_content` wraps
across the scroll axis only: a horizontal `ListView` is as tall as its tallest
cell and a vertical one as wide as its widest; a `GridView` wraps to its
tallest column or widest row, the spacing between the views of the group
included; `android:padding*` is added, and under a parent that offers a bound
the result never exceeds it. Along the scroll axis `wrap_content` still fills
the parent, because wrapping there would mean measuring every item in the
adapter. `GridPatternView` sizes its cells from the view (`parchment_ratio`
and the pattern), so it has nothing to wrap to and fills the parent on both
axes whatever it is asked for.

The wrapped size comes from the cells present when the view is measured: the
cells already laid out, or, before the first layout, the cells that fill the
viewport from the start position, obtained through the recycler and handed
back to it. When a layout then draws a cell larger than that — one scrolled
into view, or one the layout places before the start position after a
rotation — the view asks for one more layout and grows to it on the next
measure, so a scroll through cells of varying size across the scroll axis
resizes the view as they come in; it grows on its own, but shrinks only on the
next measure something else asks for, a data set change or a parent
re-layout, so it keeps its size after the larger cell scrolls out. Cells of
varying size across the scroll axis are therefore better served by a fixed `layout_height` (or `layout_width`).

```xml
<mobi.parchment.widget.adapterview.listview.ListView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    parchment:parchment_orientation="horizontal" />
```

### Dividers

`parchment_divider` paints a drawable or a colour on every edge internal to
the content and on none at the content's outer boundary. Each drawn item is
asked about its trailing edge along each axis: where another item lies across
the gap, a divider is painted in that gap, spanning exactly the run the two
items share along the other axis. An item with no neighbour on a side is at
the boundary and gets nothing, so nothing is ever painted before the first
cell, after the last, or down the outside of the content.

In a `ListView` a cell is one view and there is one axis, so that is one
divider between each pair of items and one fewer divider than there are cells
on screen. In `GridView` and `GridPatternView` a cell is a whole group, so
dividers fall both between the groups and between the items stacked inside
one: scrolling vertically, a line between the rows and a line between the
columns. Because a divider spans only what the two items it separates share,
a `GridPatternView` pattern of mixed spans is divided correctly without rows
or columns having to exist: an item that abuts two stacked ones is divided
from each of them over its own share of the edge. Every shared edge is
painted once.

The divider is decoration and takes no space of its own: it is centred in
the `parchment_cellSpacing` gap between the two items, so the gap is what
you size to make room for it. A divider thicker than the spacing overflows
evenly onto both items and is painted over them, which is also what makes
one visible when the spacing is zero. Two items that overlap, as a negative
`parchment_cellSpacing` lays them, have no gap between them and get no
divider. Where two gaps cross — the corner at which a gap between rows meets
a gap between columns — no item lies on either side of either gap, so the
crossing is left unpainted.

A divider follows the items it separates rather than the view's padding: it
begins and ends where they do, so it is inside `android:padding*` exactly when
the items are. `android:clipToPadding` decides whether a cell is clipped at the
padding; either way it never reaches the divider.

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
is `(top, left, height, width)` in grid cells; every group you add repeats in
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

The [sample app](sample/src/main/java/mobi/parchment) is a playground for
all of this. Its first page lists the high-level items — the three views and
the behaviours that cut across them: paging, snapping, circular scrolling,
dividers, selection, the scroll listener. Each opens the playground with a
preset that shows that item off, where every attribute above is a control;
Show then inflates the configured view with a status line of what its
listeners report, the action bar's Scroll to… asks for a position and calls
`smoothScrollToPosition` with it, and the info button shows the layout that
reproduces it, which Copy puts on the clipboard. Every control carries a hint
saying what its attribute does, and a control whose attribute would have no
effect under the other settings — the paging interval without
`parchment_isViewPager`, say — disables itself and says what it needs. Parchment reads its attributes
only when a view is inflated, so the playground drives them through theme
attributes: the layouts under `sample/src/main/res/layout/playground_*.xml`
reference `?attr/playground_*`, and `PlaygroundTheme` applies one overlay
style per chosen option before inflating.

### Scrolling programmatically

Two methods move the content from code, on all three views. `setSelection`
jumps: the cell holding the position is laid out at `parchment_snapPosition`
in the next layout pass and becomes the selected item, with no animation and
no scroll reported. `smoothScrollToPosition` animates there instead, through
the same path a fling takes, so everything that shapes a fling shapes it too:
`android:padding*`, `parchment_scrollWithinContent` (a cell the bound holds
short of its snap point decelerates onto the bound and rests there),
`parchment_isCircularScroll` (the shorter way round, in pixels), and a
`parchment_isViewPager` view, which it crosses page by page rather than being
held to one. It reports `settling` and then `idle` to the scroll listener, and
it selects only under `parchment_selectOnSnap`: the cell at the snap position
when it comes to rest, once; the selection is otherwise `setSelection`'s job.
The position is clamped to the adapter, so anything past the end scrolls to
the last cell and anything negative to the first. It does nothing before the
first layout, on an empty adapter, or when the cell is already at its snap
point; it is ignored while a finger is down, and a touch cancels it the way a
touch cancels a fling. Under `onScreen` it scrolls the minimum that shows the
cell whole.

```java
listView.smoothScrollToPosition(42);
```

The name follows `AbsListView`, which `android.widget.AdapterView` does not
declare, so as with `setOnScrollListener` nothing here overrides or shadows a
platform method.

### Scroll listener

`setOnScrollListener` reports scrolling on all three views. It takes the shape
of `RecyclerView.OnScrollListener` rather than `AbsListView.OnScrollListener`: a
state change and this frame's movement, not visible item positions.
`android.widget.AdapterView` declares no `setOnScrollListener`, so nothing here
overrides or shadows a platform method.

```java
public final class ParallaxHeader implements OnScrollListener {

    @Override
    public void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement) {
        mHeader.setTranslationX(mHeader.getTranslationX() + displacement / 2f);
    }

    @Override
    public void onScrollStateChanged(
            final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
        mHeader.setSelected(scrollState == ScrollState.idle);
    }
}

listView.setOnScrollListener(new ParallaxHeader(header));
```

`onScrolled` fires once per frame in which the content moved, carrying the
signed distance it moved along the scroll axis. Parchment scrolls one axis at a
time, so that is one value rather than a `(dx, dy)` pair with one half always
zero. The sign is Parchment's own, not RecyclerView's: it is the displacement
applied to the content, so positive moves cells toward larger coordinates (right,
or down when `parchment_orientation` is `vertical`) and negative moves them
toward the start. RecyclerView's `dx`/`dy` measure the viewport instead and so
carry the opposite sign; the parameter is called `displacement` rather than `dx`
to keep that difference visible at the call site.

The distance reported is what the cells actually moved, not what was asked for: a
fling that runs into the first or last cell reports only the part that landed,
and a frame that moves nothing reports nothing at all. A jump is not a scroll, so
`setSelection` and a data set change report no displacement.

`onScrollStateChanged` fires only when the state changes, so it never reports the
same state twice in a row:

| `ScrollState` | When |
|---|---|
| `dragging` | a finger is moving the content, past the touch slop |
| `settling` | the content is moving on its own: a fling, a snap, a tap-to-snap, a programmatic move, or `smoothScrollToPosition` |
| `idle` | nothing is moving |

A gesture that never passes the touch slop reports nothing at all. Both callbacks
run after the frame's layout, so a listener that reads the view sees the cells
where they landed rather than half-updated; within a frame `onScrolled` comes
first, so `idle` always means every movement has already been reported. An
animation that starts and comes to rest within a single frame, as a short snap
or a smooth scroll on a stalled main thread can, still reports `settling` and
then `idle`, after that frame's `onScrolled`.

What is reported is a change, not a snapshot: a listener set while the content is
already moving is told the current state on the next frame if it differs from the
last state reported on this view, and told nothing if it does not — so setting the
same listener twice mid-gesture does not repeat `dragging`. Setting a listener
costs no extra animation frames.

### The inherited `AdapterView` surface

The three views extend `android.widget.AdapterView`, so they inherit its whole
public API. These members answer from the layout engine's own cells and its
view-to-position map rather than from state Parchment never maintained:

| Member | What Parchment reports |
|---|---|
| `getCount()` | `getAdapter().getCount()`, or 0 with no adapter |
| `getFirstVisiblePosition()` | the first adapter position with a cell on screen; 0 when nothing is on screen |
| `getLastVisiblePosition()` | the last adapter position with a cell on screen; `INVALID_POSITION` when nothing is |
| `canAnimate()` | true when an `android:layoutAnimation` is set and the adapter has items, so the animation runs |
| `setEmptyView(View)`, `getEmptyView()` | the empty view is shown and the adapter view hidden whenever the adapter is null or empty, re-checked on every adapter change |
| `getPositionForView(View)` | the adapter position of a child, or `INVALID_POSITION` |
| `getItemAtPosition(int)`, `getItemIdAtPosition(int)` | read straight from the adapter, as on the platform widget |
| `getSelectedItemPosition()`, `getSelectedItem()`, `getSelectedItemId()`, `getSelectedView()`, `setSelection(int)` | Parchment's own selection |
| `setOnItemClickListener`, `performItemClick` and the three listener getters | unchanged framework behaviour |
| `setOnItemLongClickListener`, `setOnItemSelectedListener` | the framework's, plus Parchment's own bookkeeping |

**What "visible" means.** A cell counts as visible when it has at least one
pixel inside the viewport measured inside `android:padding*`: its end is past
the start edge and its start is before the end edge. Parchment attaches cells
slightly outside that box — one starting exactly at the end edge, and one
scrolled off the start whose `parchment_cellSpacing` gap still shows its
divider — and those are not reported as visible, so the numbers describe what
is on screen rather than what is attached.

That rule is the platform `ListView`'s everywhere except one offset. A platform
`ListView` keeps a row whose end is flush with the start edge, with none of it
on screen, and counts it; but it only ever *adds* such a row when scrolling
forward, so the same content scrolled to from the other direction reports a
different first visible position. Parchment reports the same pair whichever way
the content arrived, and therefore reports 1 where a platform `ListView`
scrolled forward by exactly one row reports 0 — and the same one row out at
that offset with padding, which is not separately tested. Everywhere else the
two agree, pinned side by side on the first layout, on an empty adapter, with
padding, with a viewport smaller than its padding, and one pixel past the start
edge (`aCellOnePixelPastTheStartEdge_matchesThePlatformListView`,
`withPadding_aCellEndingInsideTheStartPadding_isNotVisible`,
`aCellWithItsEndOnTheStartEdge_isWhereParchmentAndThePlatformListViewDisagree`).

**Positions, not cells.** A `GridView` cell holds
`parchment_numberOfViewsPerCell` adapter positions and a `GridPatternView` cell
holds one repeat of the pattern, so the first visible position is the first
position inside the first visible cell and the last is the last position inside
the last visible cell. A partly filled last cell reports only the positions the
adapter really has. A `GridPatternView` group's items do not all span the
group, so a group counted as visible can hold an item that is itself off
screen; the pair always covers every item on screen.

**Circular scrolling.** With `parchment_isCircularScroll` the reported
positions are always real adapter positions, never the wrapped ones the engine
uses internally. Across the wrap point the first visible position is greater
than the last, so read them as two independent positions rather than as a range
to iterate.

**The empty view.** `setEmptyView(View)` works past the moment it is called.
The view re-checks which of the two to show when an adapter is set, when the
data set changes or is invalidated, and when it is attached after a change it
missed while detached. Through `setAdapter` and data set changes it matches
`android.widget.ListView` step for step; on the re-attach it does more, because
a platform `ListView` unregisters its observer while detached and never
re-evaluates on attach, so it comes back showing the wrong one of the two
(`aDataSetChangeWhileDetached_isPickedUpWhereAPlatformListViewMissesIt`). With
no empty view set nothing touches the view's visibility, which is the
framework's own guard.

**Accessibility.** `AdapterView` populates the collection information on an
accessibility event from `getCount()`, `getFirstVisiblePosition()` and
`getLastVisiblePosition()`, so a screen reader was previously told a Parchment
view held no items and showed item 0 through `getChildCount() - 1`. It is now
told what the view actually holds and shows. Nothing else about accessibility
is implemented yet: there are no scroll actions and no per-item collection item
information.

**Not supported.** Three inherited getters — `getOnItemClickListener()`,
`getOnItemLongClickListener()` and `getOnItemSelectedListener()` — are `final`
in the SDK stubs and cannot be overridden; they answer from `AdapterView`'s own
fields, which is correct here because Parchment sets those listeners through
`super`.

`AdapterView` refreshes its focusable state from its private
`mDesiredFocusableState` and `mDesiredFocusableInTouchModeState` when the data
set changes, and Parchment does not: a view made focusable while its adapter
had items stays focusable when the adapter empties, where a platform `ListView`
would not
(`theFocusableState_isNotRefreshedOnADataSetChange_unlikeThePlatformListView`).
Keeping Parchment's own copy of that desired state means overriding
`setFocusable(int)`, which `AdapterView` declares only from API 26 while
Parchment's `minSdk` is 21, so it is left alone rather than done behind an API
check that nothing here can test.

`setAdapter` does not tear the engine down — by design, so a scroll in flight
is not thrown away — so the cells drawn from the previous adapter stay on
screen until the next scroll or data set change. While that is true the view
reports no visible positions rather than positions the new adapter does not
have: `setAdapter(null)`, or an adapter shorter than the drawn cells, reports
0 and `INVALID_POSITION`
(`setAdapterToNull_reportsNothingVisibleEvenThoughTheEngineKeepsItsCells`,
`replacingTheAdapterWithOneThatIsShorterThanTheDrawnCells_reportsNothingVisible`).

A detached view reports nothing visible, because the engine drops its cells on
detach; a platform `ListView` keeps its children and keeps reporting them
(`aDetachedView_reportsNothingVisible_whereAPlatformListViewKeepsItsChildren`).

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
| `parchment_scrollWithinContent` | boolean | Keep the content inside the view: the first cell stops at the start edge and the last at the end edge; see Snapping |
| `parchment_isViewPager` | boolean | One page per gesture, ViewPager style |
| `parchment_viewPagerInterval` | integer, or `viewport` | How far one ViewPager gesture pages. `viewport` (or `0`, or unset, the default) advances every cell that fits the viewport whole; `N` advances exactly N cells. Values below zero are read as `viewport` |
| `parchment_divider` | drawable or colour | Drawn on every edge internal to the content and on none at its outer boundary: between adjacent cells and between the items stacked inside one |
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

### Snapping

`parchment_snapToPosition="true"` settles the nearest cell on `parchment_snapPosition`
after a drag or fling. With `start` or `end` that includes the last or first cell:
the content can be dragged until the last cell sits at the start edge, leaving the
rest of the view empty, which is what lets `parchment_selectOnSnap` select that
cell. `parchment_scrollWithinContent="true"` keeps the content inside the view
instead: the first cell never moves inside the start edge and the last never
moves inside the end edge, so with `start` the last cell stops with its end at
the view's end, with `end` the first cell stops with its start at the view's
start, and with `center` both hold. Content shorter than the view does not
scroll and sits at the snap position: at the start, at the end, or centred. The
content then rests either on a cell at the snap position or at one of those two
ends, whichever is nearer, so a drag, fling, page, tap or `setSelection` that
runs into an end stops there even when the cells do not divide the view evenly; a cell larger
than the view is the exception and rests at its snap point, as under `onScreen`.
At an end, `parchment_selectOnSnap` selects the cell that would have snapped
there: the nearest of the cells the bound holds short of their snap point.
`onScreen` already keeps the content inside the view and circular scrolling has
no ends, so the attribute changes nothing under either.

`parchment_selectOnSnap` selects the cell a snap lands on. Under `onScreen`, which
circular scrolling forces, every cell fully inside the view is at the snap
position, so a stop selects only when one cell alone is there: the cell pushed
against an end, or the only cell that fits the view. A tap's selection otherwise
stands.

```xml
<mobi.parchment.widget.adapterview.listview.ListView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parchment:parchment_snapToPosition="true"
    parchment:parchment_snapPosition="start"
    parchment:parchment_scrollWithinContent="true" />
```

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
sample/                           Demo app: a playground for every attribute of the three views
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

Every version is on Maven Central and on the
[GitHub Releases](https://github.com/TinkerNorth/parchment/releases) page with
the AAR attached. Notable changes are tracked in [`CHANGELOG.md`](CHANGELOG.md).

To cut one: set `VERSION_NAME` in `gradle.properties`, retitle the
`[Unreleased]` section of `CHANGELOG.md` as that version with the date,
merge, then push a bare version tag (`git tag 2.0.1 && git push origin 2.0.1`).
`release.yml` refuses a tag that does not match `VERSION_NAME`, runs the fast
CI gates, publishes to Maven Central signed, and creates the GitHub Release
from the changelog section. The Central Portal credentials and the signing
key are repository secrets.

## Acknowledgements

Parchment was written by Emir Hasanbegovic, with Anthony Tarantini as the
other original contributor. Thanks also to everyone who filed issues and
pull requests against the original `EmirWeb/parchment` repository.

## License

Apache-2.0. See [`LICENSE`](LICENSE).
