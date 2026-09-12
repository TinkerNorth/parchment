# Architecture

A map of the library aimed at people working in it. The public surface is
the three views and their XML attributes (see the README); everything
below is how they are built.

## Pipeline

```
Adapter (any android.widget.Adapter)
        │
        ▼
  AdapterViewManager        recycled-view pool keyed by getItemViewType()
        │  getView(position) / recycle(view)
        ▼
  LayoutManager<Cell>       owns the visible cells, offset, snap, circular wrap
   ├── ListLayoutManager        Cell = View
   ├── GridLayoutManager        Cell = Group (a wrapping row or column)
   └── GridPatternLayoutManager Cell = GridPatternGroup (one repeat of the pattern)
        │  add/remove views, position them
        ▼
  AbstractAdapterView       the ViewGroup: onMeasure/onLayout, touch, saved state
        ▲
        │  scroll / fling / snap displacement per frame
  ChildTouchGestureListener → AdapterAnimator → ScrollAnimator (android.widget.Scroller)
```

## The layout pass

`AbstractAdapterView` runs the same frame step from two places: `onLayout`
when the framework lays the view out (size change, adapter change,
`setSelection`), and `onAnimationFrame` on every Choreographer frame while
anything moves. The animation path is scheduled with `postOnAnimation`
through the `AnimationFrameScheduler` the animator holds, runs the step
against the view's current bounds, and invalidates; it never calls
`requestLayout()`, so a fling does not re-measure the ancestor tree once
per frame. If a real layout is already pending when the frame fires, the
frame yields and the layout pass carries the animation forward.

The step itself:

1. `ChildTouchGestureListener.computeScrollOffset()` advances the active
   animation (fling, snap, page, or programmatic jump) and produces an
   `Animation` holding this frame's displacement.
2. `LayoutManager.layout(...)` applies the displacement to `mOffset`, then
   walks cells from `mStartCellPosition`: cells that scrolled off the start
   are recycled and the start position advances; new cells are pulled from
   the adapter at the end until the viewport is covered (and symmetrically
   in the other direction).
3. Each cell's views are added through `AdapterViewHandler.addViewInAdapterView`
   (which is `addViewInLayout`, so no re-layout storm) and positioned with
   `ScrollDirectionManager`, which maps start/end/size onto left/right/width
   or top/bottom/height depending on orientation.
4. If the animation is still running, the view asks the scheduler for the
   next frame; when it settles with `parchment_snapToPosition` on, the
   `SnapPositionInterface` computes the displacement to the nearest cell
   and a snap animation starts.

The layout managers never call `getLeft()`/`getTop()` directly; that is
the rule that keeps one engine working for both orientations.

## The draw pass

`AbstractAdapterView.dispatchDraw` draws the cells through `super`, then
hands the canvas to `CellDivider`. A divider belongs on every edge internal
to the content and on none at the content's outer boundary, and `CellDivider`
gets that by working in edges rather than in full-breadth lines. It walks the
drawn items — the child views themselves, not the cells — and asks each one
about its trailing edge along each of the two axes: where another drawn item
lies across the gap, a divider is painted in that gap spanning exactly the run
the two items share along the perpendicular axis. An item with no neighbour on
a side is at the content boundary and gets nothing, so the boundary rule falls
out instead of being a case, and `GridPatternView`'s mixed spans and
T-junctions are handled without rows or columns having to exist.

Only trailing edges are considered, which is what paints each shared edge
exactly once: `CellEdges.isAcrossTheEndEdge` holds for at most one of an
ordered pair, because it requires the neighbour to reach further than the
item. The same asymmetry means the size pass and the breadth pass can never
both claim one pair: a pair separated along both axes would have to overlap on
neither, and each pass requires a positive overlap on the other axis
(`gridDivider_onAnEdgeSharedByTwoItems_isDrawnOnlyOnce`).

The two passes are two named methods rather than one parameterised by an axis
strategy. They read the same four spans through `ScrollDirectionManager` with
the roles of the axes swapped, and they fill the `Rect` in opposite orders; a
strategy family next to `ScrollDirectionManager` would be a second thing
called an "axis" and would invite exactly the `getLeft()`/`getTop()` confusion
the engine rule exists to stop. What they share is the geometry, and that
lives in `CellEdges`: whether a neighbour lies across an end edge, the overlap
of two spans, whether an overlap is real, and whether a third item stands in
the gap inside the band a divider would span. `CellEdges` takes scalars and
returns scalars, the shape `getDividerStart` already had, so every one of
those facts is pinned on its own in `CellEdgesTest` without a layout manager
and without weakening anything's visibility.

The third of those facts is what keeps a divider from being drawn across an
item that stands between two others: a candidate occludes the edge when it is
across the item's end edge and the neighbour is across *its* end edge, and it
reaches the band. Without it, three items in a line would be divided 1-2, 2-3
and also 1-3, the last drawn straight through the middle one.

Because this runs on every frame while anything moves, the neighbour search is
bounded by structure rather than cached. The candidates for an item in a drawn
cell are the items of that cell and of the next one, and nothing else: cells
are laid end to end along the scroll axis, so an item two cells away is either
not adjacent or separated by a hole rather than by a gap. That makes the work
per frame linear in the number of drawn items, with a constant set by the
items in one cell — one for `ListView`, `parchment_numberOfViewsPerCell` for
`GridView`, the pattern's item count for `GridPatternView` — and independent
of the adapter's size and of how far the view has been scrolled. Nothing is
computed once per layout pass and kept, because the layout pass runs on every
frame too while scrolling, so a cache would save nothing and would have to be
allocated and invalidated on every cell that is recycled or prepended
mid-gesture.

Nothing on the path allocates: one `Rect` field is refilled per divider
(`everyGridDividerOfEveryFrame_isMeasuredIntoTheSameBoundsRect`), every loop
is indexed rather than iterated, and the items are reached through
`LayoutManager.getDrawnCellViewCount` and `getDrawnCellView`, which index into
a cell instead of copying its view list the way `getViews` does. Those two,
with `getDrawnCellCount`, are `protected final`: `protected` carries the
package access `CellDivider` and the tests need, and `final` keeps a subclass
from overriding one and silently relocating every divider. What a `Drawable`
does inside its own `draw` is its own business.

Dividers are decoration and never enter the layout: the items sit where
`parchment_cellSpacing` puts them and the divider is centred in the gap
between two items' boundaries, measured as `gapStart + (gapEnd - gapStart) / 2`
rather than `(gapStart + gapEnd) / 2`, because a gap that straddles the
leading edge has a negative start and integer division truncates toward zero
(`dividerStart_inAGapThatStartsBeforeTheOrigin_centresTheDividerInTheGap`).
Painting after the children is what makes a divider thicker than the
spacing — a zero spacing included — visible rather than hidden under the item
it overlaps. Where a gap between rows crosses a gap between columns no item
abuts either gap, so the crossing is left unpainted and the grid reads as a
grid (`dividerInXmlOnAGridView_whereTwoGapsCross_paintsNothing`).

A divider begins and ends where the items it separates do, and knows nothing
about padding. It does not need to: the layout managers place cells inside
`android:padding*`, so a divider that follows them is inside the padding too
(`divider_withPaddingSet_spansTheRowsItSeparatesAndNoFurther`). This is the one
place the new rule moved an existing line. `ListLayoutManager.layoutCell`
centres a row in the *whole* breadth rather than inside the padding box, so
with asymmetric padding the row sits off-centre of that box; the divider used
to span the padding box and now spans the row, which is where the content
actually is. `android:clipToPadding` is applied by `ViewGroup.dispatchDraw`
and restored before it returns, so it never clips the divider either way.

`parchment_gravity` can leave a view shorter than its row along the scroll
axis, and the divider follows the view there too: two items of different
lengths in one row are each divided from their own neighbour, in their own
gap, so the line is not continuous across the row. That is the rule working
rather than failing — the alternative, snapping every divider onto the cell
boundary, would put a line part-way inside a short view
(`gridDivider_withAShortViewPlacedByGravity_followsTheViewRatherThanTheCell`).

`CellDivider` still resolves its thickness once, in its constructor, from
either `parchment_dividerSize` or the drawable's intrinsic size, and nothing
selects between algorithms per frame.

With `parchment_isCircularScroll` on, the drawn cells already wrap, so the
divider between the last cell and the first is just a divider between two
adjacent drawn cells and needs no case of its own.

A cell that scrolls off the start keeps its divider for as long as the gap
after it is on screen, without the draw pass knowing anything about it:
`layout` recycles a cell once its end passes 0, but the backward pass then
prepends cells while `mOffset` is still above 0, and `mOffset` is above 0
exactly when part of that gap is still visible
(`divider_whenACellScrollsOffTheStart_isStillDrawnWhileItsGapIsOnScreen`).

## Recycling

`AdapterViewManager` keeps a `Queue<View>` per adapter view type plus a
`Map<View, Integer>` from live view to its type. `getView` polls the queue
for the type, hands the view to `Adapter.getView` as `convertView`, and
re-measures only when the adapter returned a different view or the view
asked for layout. `recycle` puts a removed view back on its type's queue.
There is no `ViewHolder`: the adapter's `getView` is the whole contract, as
with the platform `ListView`.

`DataSetObserverManager` forwards `notifyDataSetChanged` to the
`LayoutManager`, which invalidates all cells; there is no diffing.

## Cells

The `Cell` type parameter is what differs between the three views:

| View | Cell | Cell size |
|---|---|---|
| `ListView` | `View` | the view's measured size along the scroll axis |
| `GridView` | `Group` | `parchment_numberOfViewsPerCell` views laid across the breadth; the tallest (or widest) view sets the cell size, `parchment_gravity` places the rest |
| `GridPatternView` | `GridPatternGroup` | one repeat of a `GridPatternGroupDefinition`: a list of `GridPatternItemDefinition(top, left, height, width)` in grid units; `parchment_ratio` fixes the unit's aspect |

`GridPatternLayoutManager` walks the adapter through the group
definitions in order, so a pattern of "one hero, two small" followed by
"three small" repeats every five items. With no definitions it degrades to
a plain list (`GridPatternLayoutManagerNoDefinitionTest`).

## Snapping

`snapposition/` holds one strategy per `parchment_snapPosition` value. Each answers
two questions for a cell: where it should sit when snapped, and how far
the content must move to get it there. `LayoutManager` picks the strategy
once from the attributes and holds it as `mSnapPositionInterface`;
`onScreen` is the default for a view inflated from XML, `center` for one
built in Java, and neither moves content on its own.

With `parchment_snapToPosition` on, a fling is retargeted when it starts:
`LayoutManager.getFlingSnapAdjustment` takes the distance the fling would
travel, finds the cell that would land nearest the snap position (walking
the visible cells, and extrapolating with the edge cell's size plus spacing
when the end lies beyond them, never past the first or last cell unless
scrolling is circular), and the animator moves the fling's end point there.
The fling keeps its physics; only its end changes, so there is one motion
from finger-up to rest. The snap that follows a stop still runs as a safety
net for anything the extrapolation could not know, such as cells of
different sizes.

## Circular scrolling

`parchment_isCircularScroll` is handled entirely in `LayoutManager`: adapter positions
are wrapped modulo `getCount()` when cells are fetched, and the start/end
bounds that stop a normal scroll are disabled. The adapter sees only real
positions. `GridLayoutManagerCircularScrollTest` covers the wrap points.

## ViewPager mode

`parchment_isViewPager` changes the gesture interpretation, not the layout: a
completed gesture advances one page in the fling direction, however far the
finger travelled. `parchment_viewPagerInterval` says what a page is. Zero, the
value an absent attribute already yields and the name `viewport` also resolves
to, pages by the run of cells that fit the viewport whole; a positive N pages
by exactly N cells.

`AdapterAnimator.onFling` asks `LayoutManagerBridge` for the distance instead
of handing the velocity to the scroller. What the scroller is handed is that
distance *minus* how far the gesture has already dragged the content, so the
finger and the animation together move exactly one page from where the gesture
started. That running total counts only movement the bounds actually allowed:
a frame the over-draw clamp refuses adds nothing to it, or a gesture held at
either end of the list would answer with the drag it was denied.

`LayoutManager` measures that distance in `layout`, only when
`parchment_isViewPager` is on, whenever a new animation id arrives and before
that frame's displacement is applied. A new id arrives when a gesture starts
and again on every layout taken while the view is at rest, so the distance is
always the one measured from the layout the gesture started from. It takes the
cell nearest the snap position as the anchor, and the distance is the gap
between the anchor's snapped start and the start of the cell the page lands
on, in each direction separately (`mViewPageDistanceForward` and
`mViewPageDistanceBack`). Measuring from starts rather than summing sizes is
what makes cells of different sizes page correctly and puts the landing point
on a cell boundary even when the gesture starts part-way through a cell.

Only the choice of landing cell differs between the two modes, so that choice is
all that a mode is. `pageinterval/` holds one strategy per mode, as
`snapposition/` holds one per snap position: `PageIntervalInterface` asks for the
cell index a page lands on in each direction, `CellCountPageInterval` answers it
by counting and `ViewportPageInterval` by walking the viewport.
`LayoutManager`'s constructor reads `parchment_viewPagerInterval` once, hands the
value to `PageIntervalSelector` to pick between the two, and keeps the answer as
`mPageIntervalInterface`. Nothing can set the interval afterwards, so choosing
once gives the same answer the old per-call branch gave. That selection is a small
public factory rather than the private switch `snapposition/` uses, which is the
one place the two packages differ: it lets the tests exercise the real choice
instead of a copy of it.

`ViewportPageInterval` takes the anchor and the room a page has as parameters and
calls back into `LayoutManager` for the drawn cells and the cell spacing, the way
the snap strategies call back into it. `CellCountPageInterval` needs only the
interval it was built with and touches no cell. Neither is consulted per frame:
`setViewPageDistances` runs when a new animation id arrives, once per gesture.

`CellCountPageInterval` takes the cell `N` along from the anchor.
`ViewportPageInterval` walks out from the anchor while the next cell still fits
entirely inside the viewport, and lands on the first one that does not; a cell
larger than the viewport is simply the first cell that does not fit, which is
why it stays one cell per gesture without a special case. The walk mirrors
backwards, so a page back covers the run of cells that would fill one viewport
ending at the anchor. A page always advances at least one cell.

The room a page has is `maximumPageSize`: the distance from where the anchor
will sit once snapped to the end of the size inside the padding, not the whole
of that size. The two are the same only when the anchor snaps to the start
edge. With `center` or `end`, or with `onScreen` resting part-way through a
cell, the anchor sits further in and less of the viewport is left for the page,
so measuring against the whole size would count a cell that is only partly
visible and skip it for good.

Cells past the ends of the visible run are extrapolated from the edge cell's
size plus spacing and capped at the adapter's ends, the same way
`getFlingSnapAdjustment` extrapolates, so a gesture at the last cell asks for
no movement rather than running off the end. Circular scrolling lifts that cap
and the positions wrap, which is also why the cap is not what ends the walk:
the walk stops when the page no longer fits, and the guard that the next index
names a further cell is what stops it when the cap, a zero cell size or a
negative `parchment_cellSpacing` leaves the start where it was. Because the
extrapolation knows only the edge cell's size, a page back over cells that are
no longer drawn is exact only while those cells match it.

The snap position is *not* forced: `parchment_snapPosition` applies as it does
everywhere else (`onScreen` for a view inflated from XML), and the anchor is
found through the same `SnapPositionInterface` the snaps use, so paging works
from wherever a cell rests. It composes with `parchment_isCircularScroll`.

## Touch

`AdapterViewGestureDetector` wraps `GestureDetector` and forwards the
`ACTION_UP`/`ACTION_CANCEL` the platform detector swallows.
`ChildTouchGestureListener` turns scrolls and flings into `AdapterAnimator`
state (`scrolling`, `flinging`, `snapingTo`, `animatingTo`, `jumpingTo`,
`notMoving`) and decides whether a child consumed the touch, so item
clicks still reach `OnItemClickListener` through `AdapterView.performItemClick`.

## Saved state

`LayoutManagerState` (a `View.BaseSavedState`) persists the scroll offset
and the first visible cell's adapter position, so rotation restores the
same content position without the adapter's help.

## Where to look

| Question | File |
|---|---|
| Why did a view get re-measured? | `AdapterViewManager.getView` |
| Why did scrolling stop early / overshoot? | `LayoutManager.layout` bounds handling, `*OverScrollTest` |
| Why did the snap land in the wrong place? | the strategy in `snapposition/`, `getCellDisplacementFromSnapPositionTests` |
| Why is padding wrong? | `ListLayoutPaddingTest`; padding is applied in the layout managers, not the views |
| Why is the divider missing or in the wrong place? | `CellDivider` and the edge geometry in `CellEdges`; `CellDividerTest`, `CellDividerGroupTest`, `CellEdgesTest`, `CellDividerPaintTest` |
| Why did a ViewPager gesture land where it did? | `LayoutManager.setViewPageDistances` and the strategy in `pageinterval/` + `ViewPagerTest`, with each method it is built from in `LayoutManagerPagingMethodsTest` |
