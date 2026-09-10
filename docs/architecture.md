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
5. Last, `ScrollListenerDispatcher` reports the frame to an `OnScrollListener`
   if one is set. Being last is the contract: a listener that reads the view
   sees the cells where this frame put them, not half-updated
   (`theCallbacks_runAfterTheFramesLayout_soAListenerSeesTheCellsWhereTheyLanded`).

The layout managers never call `getLeft()`/`getTop()` directly; that is
the rule that keeps one engine working for both orientations.

## Reporting the frame

What the listener is told about movement is not `animation.getDisplacement()`.
`LayoutManager.layout` records `mFrameDisplacement`, the displacement it actually
applied: the frame's displacement plus the over-draw adjust `setOffset` returns,
plus any correction `correctOverScroll` makes afterwards. That is the number by
which the cells moved, so a fling clamped at an end reports only the part that
landed. The recycling inside `layoutCells` also moves `mOffset`, but it moves
`mStartCellPosition` with it and the cells do not move, so the offset's own delta
would be the wrong number to report. A reset — every cell scrolled off, so
`resetWhenNoCellsAreDrawn` puts the content back at an end — contributes nothing,
because that is a jump and not a scroll; any over-scroll correction the same
frame makes afterwards still counts, because that one does move the cells.
`setSelection` and a data set change are jumps too, and move `mOffset` outside
`layout` altogether, so they report nothing
(`setSelection_isAJumpAndNotAScroll_soItReportsNoDisplacement`).

The state comes from `AdapterAnimator.State` through `ScrollState.from`:
`scrolling` is `dragging`, `notMoving` is `idle`, and everything that moves the
content on its own — `flinging`, `snapingTo`, `animatingTo`, `jumpingTo` — is
`settling`. The dispatcher keeps the last state it reported and says nothing when
it has not changed, which is what swallows the `notMoving` that `setState`
passes through on its way to `snapingTo` when a drag is released off the snap
position.

`AdapterAnimator.setState` asks the dispatcher whether the new state is one the
listener has not been told about yet, and if so requests a frame. Every state
that moves anything already requests one; this covers the state that does not,
`notMoving` reached from a drag released exactly on the snap position, which asks
for no animation and so would otherwise never reach a frame to be reported. The
question is asked of the dispatcher rather than answered in the view so that a
view with no listener schedules nothing extra
(`onUp_afterADragThatNeedsNoSnap_withNoScrollListener_requestsNoFrame`), and
`AbstractAdapterView.setOnScrollListener` asks it too, which is what tells a
listener set part-way through a gesture what the view is already doing.

The asking is split out of `moveToState`, which does the work and recurses into
itself when a stop hands off to a snap, so the question is asked once after the
state has settled rather than once per recursion. Asking during the recursion
would schedule the snap's first frame before `ScrollAnimator.snapTo` starts the
scroller, and that frame would then land on the animation's own start and move
nothing — a whole frame per gesture, spent only because a listener was attached
(`aDragReleaseSnapAndRest_withAListener_runsTheSameFramesAsWithout`).
`mIsInsideAFrame` covers the same ground from the other side: `computeScrollOffset`
and `onFrameLaidOut` bracket the frame step, and a state change inside it needs no
frame of its own because the step dispatches at its end.

The dispatcher reports a change, not a snapshot. It keeps the last state it
reported for the view, not for the listener, so a listener set mid-gesture is
told the current state only when it differs from that, and setting the same
listener again does not repeat it
(`reAttachingTheSameListenerMidDrag_doesNotReportDraggingTwice`). It also reads
the listener field again between the two callbacks, so a listener that removes or
replaces itself from inside `onScrolled` is not called again in that frame.

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
exactly once on a given axis: `CellEdges.isAcrossTheEndEdge` holds for at most
one of an ordered pair, because it requires the neighbour to reach further than
the item (`isAcrossTheEndEdge_forAPairOfItems_holdsInOneDirectionOnly`).

A pass draws in a gap, so before anything else it asks whether there is one:
`CellEdges.isAGap` holds when the neighbour starts no earlier than the item
ends along this axis, and a pair that fails it overlaps on this axis and gets
nothing from this pass. That is the whole of what keeps the two passes off the
same pair. Ordering on an axis does not imply separation on it — a negative
`parchment_cellSpacing` lays items over each other in both directions — so a
pair can be across the end edge on both axes, and without the precondition each
pass would divide it, at two different places. With it, a pair separated on one
axis is drawn once, by that axis's pass, over the run the two share on the
other; a pair overlapping on one axis and separated on the other gets exactly
one segment, over the overlap
(`gridPatternDivider_betweenDiagonalItemsOverlappingAcrossTheBreadth_dividesOnlyTheOverlap`);
and a pair that overlaps on both axes has no gap for either pass to fill and
gets no divider at all (`gridDivider_withANegativeCellSpacing_isNotDrawn`,
`divider_withANegativeCellSpacing_isNotDrawn`). A gap of zero is still a gap,
which is why a zero spacing still draws
(`isAGap_forANeighbourTouchingTheEndEdge_isTrue`).

The two passes are one method over a strategy, the way `snapposition/` and
`pageinterval/` hold one algorithm each. They read the same four spans with the
roles of the axes swapped and fill the `Rect` in opposite orders, so
`divideraxis/` holds that swap: `DividerAxisInterface` answers `getStart`,
`getEnd`, `getBandStart`, `getBandEnd` and `setDividerBounds`, and
`SizeDividerAxis` and `BreadthDividerAxis` are built once in `CellDivider`'s
constructor and reused every frame. Both reach orientation only through
`ScrollDirectionManager`, so there is still exactly one place in the engine that
knows about left and top. What the passes share besides the axis is the
geometry, and that lives in `CellEdges`: whether a neighbour lies across an end edge, the overlap
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
are laid end to end along the scroll axis, so an item two cells away is never
the nearest thing across a gap. Inside one cell there is no such bound, and a
`GridPatternGroupDefinition` that leaves a grid position empty is divided across
that empty position as though it were a gap, because nothing in the draw pass
can tell a hole from spacing
(`gridPatternDivider_acrossAHoleInThePattern_isStillDrawn` pins that), but only
while nothing stands anywhere in the gap inside the band: `isGapOccupied`
answers for the whole edge, so an item beside the hole occludes the edge across
the hole too and nothing is drawn there
(`gridPatternDivider_acrossAHoleWithAnItemBesideItInTheGap_isNotDrawn`). In a
pattern without holes that is the right answer, because whatever fills the rest
of the band is a neighbour in its own right and gets its own divider.

The work per frame is therefore linear in the number of drawn cells
(`gridDivider_theWorkItDoes_growsWithTheDrawnItemsRatherThanTheirSquare`) and
independent of the adapter's size and of how far the view has been scrolled,
but the cost of one cell is cubic in the items of two adjacent cells, not
linear in them: for each item and each axis the neighbour scan reads every
item of this cell and the next, and for each neighbour that passes the cheap
tests `isGapOccupied` reads them all again. A cell of n items with N items in
it and the next together costs up to 2·n·N·(N+1) item reads — 12 for
`ListView`, 80 for a `GridView` with two views per cell, 1,100 for one of the
sample's five-item pattern groups. Measured rather than bounded, a frame that
draws all three of the sample's pattern groups, eleven items, makes 384 item
reads and paints 20 dividers, and each read is an indexed list lookup and two
`View` getters, so the frame spends microseconds here against its 16 ms. A
pattern of dozens of items per group would not: at thirty items per group the
bound is 219,600 reads per cell. The fix for that is to take the occupancy scan
out of the neighbour loop — only a candidate across the item's end edge can
occlude it, and the neighbour loop already visits every one of those, so a
sweep over them in order of their starts, marking the run of the band each
covers into a reused array, finds the neighbours and the occluders in one pass.
That is a follow-up, not part of the change that introduced the rule. Nothing
is computed once per layout pass and kept, because the layout pass runs on
every frame too while scrolling, so a cache would save nothing and would have
to be allocated and invalidated on every cell that is recycled or prepended
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
about padding. It follows the content, and so it is inside the padding exactly
when the content is. This is the one place the new rule moved an existing line,
and it moved it onto a pre-existing layout bug rather than away from one:
`ListLayoutManager.layoutCell` centres a row in the *whole* breadth rather than
inside the padding box, so with asymmetric padding the row sits off-centre of
that box and overhangs one padding edge. The divider used to span the padding
box and now spans the row, overhang included
(`divider_withPaddingSet_spansTheRowsItSeparatesAndNoFurther`). Fixing the
centring is a separate change; until then the divider tells the truth about
where the row is. `android:clipToPadding` is applied by `ViewGroup.dispatchDraw`
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

A strategy answers both from one private `getSnappedCellStart`: the snap
distance is that start minus `getCellStart`, the backward draw limit is that
start, the forward draw limit is that start plus `getCellSize`, and
`getAbsoluteSnapPosition` is that start. Deriving all four from one number is
what makes a snap target reachable. The draw limits are what stop a scroll, so
if the start a snap asks for is not the start the clamp allows, the clamp puts
the content back, the stop asks for the distance again, gets the same answer,
and the view snaps forever without moving
(`gridPatternCenterSnap_onceTheCellIsCentred_asksForNoFurtherMovement`).

Two things used to break that. Measuring the cell's representative view rather
than the cell holds only while that view is exactly as big as its cell, which
is true of `ListView`, whose cell is the view, and of `GridView`, whose
representative is the row's tallest view and so is what sets the row's size,
but false of a `GridPatternView` pattern with more than one row. And writing
the same halved quantity twice does not survive integer division: `center`'s
target `(size - cellSize) / 2` and its old forward limit `(size + cellSize) / 2`
truncate opposite ways once a cell is larger than the viewport, so a cell
larger by an odd number of pixels was one pixel out of reach in every view
(`centerSnap_cellTallerThanTheViewportByAnOddNumberOfPixels_settles`).

Tapping a cell snaps that cell, not the view that was tapped:
`LayoutManagerBridge.onSingleTapUp` finds the cell holding the tapped view
and asks for that cell's distance, so a tap and the settle that follows it
agree on where the content stops.

With `snapToPosition` on, a fling is retargeted when it starts:
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
| Why did the scroll listener report that? | `ScrollListenerDispatcher`, `ScrollState.from`, `LayoutManager.getFrameDisplacement` |
