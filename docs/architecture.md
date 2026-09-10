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
hands the canvas to `CellDivider`, which paints `parchment_divider` once
between each pair of adjacent drawn cells. It reads the boundaries through
`LayoutManager.getDrawnCellCount`, `getDrawnCellStart` and `getDrawnCellEnd`,
so the cell list itself stays inside the engine, and it maps thickness and
breadth onto left/top/right/bottom through `ScrollDirectionManager` like
everything else orientation-specific. Drawing runs on every frame while
anything moves, so nothing in Parchment allocates on that path: one `Rect`
field is refilled per divider, the loop is indexed rather than iterated, and
`Group` reads a row's bounds without an iterator or a boxed accumulator.
What a `Drawable` does inside its own `draw` is its own business.

Dividers are decoration and never enter the layout: the cells sit where
`parchment_cellSpacing` puts them and the divider is centred in the gap
between two cells' boundaries. Painting after the children is what makes a
divider thicker than the spacing — a zero spacing included — visible rather
than hidden under the cell it overlaps. `android:clipToPadding` is applied
by `ViewGroup.dispatchDraw` and restored before it returns, so it never
clips the divider; `CellDivider` applies `getStartBreadthPadding` and
`getEndBreadthPadding` itself instead (`CellDividerPaintTest`).

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
| `GridPatternView` | `GridPatternGroup` | one repeat of a `GridPatternGroupDefinition`: a list of `GridPatternItemDefinition(left, top, width, height)` in grid units; `parchment_ratio` fixes the unit's aspect |

`GridPatternLayoutManager` walks the adapter through the group
definitions in order, so a pattern of "one hero, two small" followed by
"three small" repeats every five items. With no definitions it degrades to
a plain list (`GridPatternLayoutManagerNoDefinitionTest`).

## Snapping

`snapposition/` holds one strategy per `parchment_snapPosition` value. Each answers
two questions for a cell: where it should sit when snapped, and how far
the content must move to get it there. `LayoutManager` picks the strategy
once from the attributes; `onScreen` is the default for a view inflated
from XML, `center` for one built in Java, and neither moves content on
its own.

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
completed gesture advances exactly `parchment_viewPagerInterval` cells (one by
default) in the fling direction, however far the finger travelled.
`AdapterAnimator.onFling` asks `LayoutManagerBridge` for the distance instead
of handing the velocity to the scroller.

`LayoutManager` measures that distance in `layout`, only when
`parchment_isViewPager` is on, whenever a new animation id arrives and before
that frame's displacement is applied. A new id arrives when a gesture starts
and again on every layout taken while the view is at rest, so the distance is
always the one measured from the layout the gesture started from. It takes the
cell nearest the snap position as the anchor, and the distance is the gap
between the anchor's snapped start and the start of the cell the interval
away, in each direction separately (`mViewPageDistanceForward` and
`mViewPageDistanceBack`). Measuring from starts rather than summing sizes is
what makes cells of different sizes page correctly, puts the landing point on
a cell boundary even when the gesture starts part-way through a cell, and
keeps a cell wider than the viewport to one cell per gesture. Cells past the
ends of the visible run are extrapolated from the edge cell's size plus
spacing and capped at the adapter's ends, the same way `getFlingSnapAdjustment`
extrapolates, so a gesture at the last cell asks for no movement rather than
running off the end. Circular scrolling lifts that cap and the positions wrap.

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
| Why is the divider missing or in the wrong place? | `CellDivider`, `CellDividerTest`, `CellDividerPaintTest` |
| Why did a ViewPager gesture land where it did? | `LayoutManager.setViewPageDistances` + `ViewPagerTest` |
| Why did the scroll listener report that? | `ScrollListenerDispatcher`, `ScrollState.from`, `LayoutManager.getFrameDisplacement` |
