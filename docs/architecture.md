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
completed gesture advances exactly one cell in the fling direction
(`AdapterAnimator` with the `mViewPageDistance`), and the snap position is
forced to `start` so pages align. It composes with `parchment_isCircularScroll`.

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
| Why does the ViewPager page twice? | `AdapterAnimator` + `ViewPagerTest` |
