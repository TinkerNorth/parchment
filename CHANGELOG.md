# Changelog

All notable changes to Parchment, newest first. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

---

## [Unreleased]

### Fixed

- The inherited `android.widget.AdapterView` members report what the view is
  actually showing instead of state Parchment never maintained. `getCount()`
  returned 0 for every adapter, because `AdapterView` fills its item count from
  an observer only its own subclasses register; it now returns the adapter's
  count, or 0 with no adapter. `getFirstVisiblePosition()` returned 0 whatever
  the view was scrolled to, and `getLastVisiblePosition()` returned
  `getChildCount() - 1`, which was right only while the content fitted the
  viewport — Parchment attaches a cell starting exactly at the end edge, so at
  the top of a ten-item list it answered 3 where the last visible position is
  2 — and never right for a `GridView` or `GridPatternView`, where one cell
  holds several adapter positions; both now come from the layout engine's own cells
  and its view-to-position map, so they are adapter positions and not child
  indices, and a partly filled last cell reports only the positions the adapter
  has. Visible means a cell with at least one pixel inside the viewport
  measured inside `android:padding*`, so a cell Parchment attaches past the end
  edge, or holds off the start to keep its divider on screen, is not reported.
  That is the platform `ListView`'s rule everywhere but one pixel: the platform
  counts a row whose end is flush with the start edge, but only when the
  content arrived from that direction, so its answer depends on how the content
  got there and Parchment's does not. With `parchment_isCircularScroll` the
  positions are real adapter positions, never the wrapped ones the engine uses,
  so across the wrap point the first is greater than the last. With nothing on
  screen the pair is 0 and `INVALID_POSITION`, as on the platform widget, which
  keeps the usual `first..last` loop empty rather than running once at -1. A
  position the current adapter does not have is never reported: `setAdapter`
  leaves the previous adapter's cells drawn by design, so while a shorter or
  null adapter is set the pair is the empty one rather than positions that
  would make `getItemAtPosition` throw.

- A data set change after a view was detached and re-attached reached the view
  but not the layout engine. `LayoutManager` registers its data set observer in
  its constructor and unregisters it in `destroy()`, which
  `onDetachedFromWindow` calls, and nothing registered it again, so after one
  detach a `notifyDataSetChanged` removed every child and redrew nothing while
  the view went on reporting the old positions. `onAttachedToWindow` now
  registers it again (`aDataSetChangeAfterAReAttach_reachesTheLayoutEngine`).

- The collection information `AdapterView` puts on an accessibility event comes
  from `getCount()`, `getFirstVisiblePosition()` and `getLastVisiblePosition()`,
  so a screen reader was told a Parchment view held no items and showed item 0
  through `getChildCount() - 1`. It is now told what the view holds and shows.

- `setEmptyView(View)` works past the moment it is called. `AdapterView`
  evaluates emptiness inside `setEmptyView` and then only from a package
  private `checkFocus`, which Parchment cannot call, so the empty view was
  decided once and never revisited: setting an empty view and then an adapter
  left the empty view up and the Parchment view `GONE` for good, and a
  `notifyDataSetChanged` that emptied or refilled the adapter changed nothing.
  The view now re-checks which of the two to show when an adapter is set, when
  the data set changes or is invalidated, and when it is attached after a
  change it missed while detached, so it matches `android.widget.ListView`
  through the same sequence. With no empty view set nothing touches the view's
  visibility, which is what the framework's own guard does.

- `canAnimate()` answered `AdapterView`'s own item count, which Parchment never
  filled, so an `android:layoutAnimation` on any of the three views never ran.
  It now answers from `getLayoutAnimation()` and `getCount()`, and the
  animation runs on the cells the engine drew
  (`aLayoutAnimation_runsOnTheCellsTheEngineDrew`).

- `ContentBound` and its `getAbsoluteSnapPosition` were package private, the
  only such type and member left in the library. Both are `public` like the
  rest of `snapposition/`.

- `parchment_selectOnSnap` under `parchment_isCircularScroll` overrode a tap.
  Circular scrolling forces the `onScreen` snap, under which every cell fully
  inside the view is at the snap position, so the stop after a tap ranked them
  all equal, the first drawn won the tie, and the listener heard the tapped
  cell and then that one, with the selection ending on it (#62). A stop now
  selects the cell the snap lands on, and when more than one drawn cell is
  already at the snap position the snap lands on none of them and the stop
  leaves the selection as it is, so a tap's selection survives its own snap
  (`circularScroll_selectOnSnap_snapToWithSeveralCellsFullyOnScreen_keepsTheSelection`,
  `circularScroll_withSelectOnSnap_aTapOnACellFullyOnScreen_keepsThatCellSelected`).
  The consequence, under circular scrolling with the flag and two or more cells
  that fit the view: a fling, a drag released, a touch at rest and
  `smoothScrollToPosition` no longer select the first fully visible cell, which
  they used to, and a drag released with no residual snap used to change the
  selection without reporting it. Cells the size of the view still select the
  cell the stop snaps on. `start`, `end` and `center` are unchanged: at most
  one cell is at their snap position. `onScreen` without circular scrolling is
  unchanged: it never selected mid-content and still selects the held cell at
  an end. `setSelection` is unchanged.

## [2.2.0] - 2026-09-17

### Added

- `smoothScrollToPosition(int)`, on all three views, animates the cell holding
  the position to `parchment_snapPosition` the way a fling gets there, where
  `setSelection` jumps. It runs at RecyclerView's programmatic speed, 25 ms
  per inch, four times a snap's: a cell already laid out is landed on with one
  decelerating scroll of exactly its snap distance; a cell that is not is
  sought at a constant speed by the distance extrapolated from the edge cell's
  size plus a viewport of runway, handed to the landing in the frame it is
  laid out, and sought again from wherever it is whenever less than a viewport
  of runway is left, so the motion stays even when the cells between were
  larger than the estimate assumed. The landing is not capped at half a
  second, so a long scroll decelerates from the seek's speed rather than
  snapping the rest of the way. It obeys every bound a fling obeys:
  `android:padding*`, `parchment_scrollWithinContent`, under which a cell the
  bound holds short of its snap point decelerates onto the bound and rests
  there, and `parchment_isCircularScroll`, under which it takes the shorter
  way round. A `parchment_isViewPager` view is crossed page by page rather
  than held to one, which needed the page clamp to learn that an animation is
  not a gesture. It reports `settling` then `idle` to the scroll listener, and
  under `parchment_selectOnSnap` selects the cell at the snap position when it
  comes to rest, once. The position is clamped to the adapter; before the first
  layout, on an empty adapter, or when the cell is already at its snap point
  nothing happens; it is ignored while a finger is down, and a touch cancels it
  like a fling. Under `onScreen` it scrolls the minimum that shows the cell
  whole. A position asked for before the first layout is not remembered; a
  pending target is a follow-up (#54, asked for in #19).

### Changed

- A jump ends a running animation. `setSelection` and a data set change now
  stop whatever was moving the content, a fling included, in the layout pass
  the jump asks for, snapping whatever residual the jump left, so the view
  rests with the incoming cell at its snap point and reports one `idle`; the
  fling used to carry on from the jumped offset. At rest nothing changes: no
  frame runs and nothing is reported.
- **Breaking:** `LayoutManager` gains an abstract `getCellBreadth`, a cell's
  extent across the scroll axis, and `measureBreadth`, which
  `AbstractAdapterView.onMeasure` asks for the view's size across it;
  `ScrollDirectionManager` gains the cross-axis spec, padding and width/height
  mappings that serve them. All are implementation details the views build and
  call for themselves, so nothing outside the library should be subclassing or
  calling them.

### Fixed

- `parchment_selectOnSnap` reported the selection a stop made only on the next
  layout pass, so a fling that ended exactly on a snap point, with no residual
  snap to run a further frame, selected the cell but did not tell the
  `OnItemSelectedListener` until something else laid the view out. A frame
  that ends at rest now reports the selection its stop made, which is what
  every `smoothScrollToPosition` landing needs
  (`aFlingThatEndsOnASnapPoint_withSelectOnSnap_reportsTheSelectionWithoutALayoutPass`).
  Two consequences to know about: under `parchment_selectOnSnap` a data set
  change at rest that keeps or grows the count now reports its unchanged
  selection one layout pass earlier, the same cell in the same place, once;
  and a jump that lands mid-animation reports its selection where the cell is
  at the jump, after which the residual snap moves it the few pixels to its
  snap point, as it already did on main with the fling still running. Under
  `parchment_isCircularScroll` with `parchment_selectOnSnap`, a tap on a cell
  reports that cell and then the cell nearest the start edge, because the
  stop's snap selects the nearest cell to the `onScreen` position circular
  scrolling forces; main did the same silently, and this is now reported
  rather than fixed.
- A fling, snap or smooth scroll that ran to rest within its first frame, as a
  short snap does and as any animation does when the main thread stalls for a
  frame, reported its displacement but no scroll state: the dispatcher reports
  a change at the end of a frame and the frame ended where it began, at
  `idle`. It now reports the state the frame ran in before the state it ended
  in, when the frame moved the content, so such a frame reports `settling`
  then `idle` after its `onScrolled` and a frame that moved nothing still
  reports nothing
  (`aSmoothScrollThatEndsInItsFirstFrame_stillReportsSettlingThenIdle`,
  `aFlingThatEndsInItsFirstFrame_stillReportsSettlingThenIdle`).
- `android:layout_height="wrap_content"` on a horizontal view and
  `android:layout_width="wrap_content"` on a vertical one used to take every
  pixel the parent had left, pushing the next sibling of a `LinearLayout` off
  the screen: `onMeasure` set the measured size to the spec size whatever the
  spec's mode, so `AT_MOST` was treated as `EXACTLY`. Across the scroll axis a
  `ListView` now measures to its largest cell and a `GridView` to its largest
  row or column, the spacing between the views of the group included, plus
  `android:padding*`, capped at the size offered under `AT_MOST` and uncapped
  under `UNSPECIFIED`. `EXACTLY` is untouched, so `match_parent` and a fixed
  size lay out exactly as before. Along the scroll axis `wrap_content` still
  fills the parent, since wrapping there would mean measuring every item in the
  adapter, and `GridPatternView`, whose cells are sized from the view, still
  fills it on both axes. The size comes from the cells present at measure time:
  the cells laid out, or before the first layout the cells that fill the
  viewport from the start position, obtained through the recycler and handed
  back to it the way the platform `ListView` measures under `AT_MOST`. That
  first estimate cannot see a cell the layout back-fills before the start
  position, one the over-scroll correction pulls in, or one a scroll reveals
  later, so a layout that draws a cell larger than the size it was measured to
  asks the view for one more layout, and the next measure, which reads the
  drawn cells, grows the view to it. Cells of varying size across the scroll
  axis are therefore better served by a fixed size, because the view resizes
  as they come into view (#59, the remaining half of #10).
- A horizontal `ListView` gave its children the mode of its own *width* spec as
  their height mode, and a `GridView` did the same for the views it obtains for
  a jump. They now take the mode of the height spec. Under `wrap_content` that
  is what lets a `match_parent` child measure to its content rather than to
  the whole height the parent offered; under a bounded width and an exact
  height, a horizontal list inside a `HorizontalScrollView` or a
  `wrap_content`-wide parent, a `match_parent` child is now given the exact
  height instead of a bound it measured itself to zero against. Under `EXACTLY`
  on both axes nothing changes.

## [2.1.0] - 2026-09-16

### Added

- `parchment_scrollWithinContent`, on all three views, keeps the content inside
  the view: the first cell's start never moves inside the view's start edge and
  the last cell's end never moves inside its end edge, the bound that `onScreen`
  has always applied. With `start` the last cell now stops with its end at the
  view's end instead of being dragged to the start edge with empty space after
  it, `end` is the mirror, and `center` holds both ends; content shorter than
  the view does not scroll and sits at the snap position, at the start, at the
  end or centred. The content rests either on a cell at the snap position or at
  one of the two ends, whichever is nearer, so a drag, fling, page, tap or
  `setSelection` that runs into an end stops there and asks for nothing further,
  whether or not the cells divide the view evenly; a cell larger than the view is the exception,
  and rests at its snap point as it does under `onScreen`. At an end,
  `parchment_selectOnSnap` selects the cell that would have snapped there: the
  nearest of the cells the bound holds short of their snap point. A page is
  measured from where the anchor cell would snap without the bound, so paging
  back from an end returns to the page the gesture came from. Content that fits
  the view is put in place by the same correction on the first layout, so with
  `end` or `center` that first layout already selects, as `onScreen` has always
  done for short content. `android:padding*` bounds the content the way it
  bounds a snap. The attribute is off by default and is opt-in because the old
  behaviour is deliberate: letting every cell reach the snap point, the last and
  first included, is what lets `parchment_selectOnSnap` select every cell, and a
  layout that relies on that must not change under it. It does nothing under
  `onScreen`, which already keeps the content inside the view, or under
  `parchment_isCircularScroll`, which has no ends (#21).

### Changed

- **Breaking:** the `LayoutManagerAttributes`, `GridLayoutManagerAttributes` and
  `GridPatternLayoutManagerAttributes` constructors take the new flag, after the
  snap position; `SnapPositionInterface.getSnapToPixelDistance` takes the drawn
  cells and the interface gains `getCellSettleDistance` and
  `getUnboundedSnapToPixelDistance`. All are implementation details the views
  build and call for themselves, so nothing outside the library should be
  constructing or implementing them.

### Fixed

- A drag that pushes the content past its end used to hold the last cell at the
  snap position and then move the content one cell back under the finger,
  reporting a settling state and firing a second selection for
  `parchment_selectOnSnap`. The clamp stopped the animation from inside the
  frame, before the correction had been laid out, so the snap that a stop hands
  off to measured the cells where they had been rather than where they were. The
  stop and the selection now run once the frame is laid out: the content stays
  held on the last cell, reports no settling state, and selects it once. The
  cell selected at an end is the cell nearest the snap position among those laid
  out, which without `parchment_scrollWithinContent` is the held cell as before,
  and under `onScreen`, whose every visible cell is at its snap position, is
  still the held cell.

## [2.0.0] - 2026-09-15

The first work on Parchment since 2014. Apart from the new cell divider,
behaviour of the views is unchanged; everything around them is new.
Published to Maven Central as `mobi.parchment:parchment:2.0.0`, the same
coordinates as 1.x.

### Upgrading from 1.6.x

- Depend on `mobi.parchment:parchment:2.0.0`; the artifact is an AAR now,
  not an `apklib`. Minimum SDK is 21.
- Every Parchment XML attribute gained a `parchment_` prefix:
  `orientation` is `parchment_orientation`, `cellSpacing` is
  `parchment_cellSpacing`, and so on for all of them. Declare the namespace
  as `xmlns:parchment="http://schemas.android.com/apk/res-auto"`. The
  old names are not kept as aliases, so a layout that still uses one fails
  to build rather than silently ignoring it. The README lists every
  attribute.
- Class names, packages and the public Java API are unchanged. Only two
  `protected` hooks changed shape, `createAdapterViewInitializer` and the
  constructors of `AdapterAnimator` and `ChildTouchGestureListener`; a
  subclass that overrides or calls them has to pass the new parameters on.

### Highlights

- `setOnScrollListener` on every view, shaped like
  `RecyclerView.OnScrollListener`, with the state and the displacement.
- `parchment_divider` and `parchment_dividerSize` draw a divider between
  cells, in every view.
- `parchment_viewPagerInterval` pages a fixed number of cells instead of a
  viewport.
- A snapping view comes to rest after a gesture instead of animating
  forever, and animation frames no longer re-lay-out the whole ancestor tree.
- The sample app is a playground that reaches every attribute and prints
  the XML that reproduces what is on screen.

Everything below is the full record.

### Changed

- **Breaking:** `AbstractAdapterView.createAdapterViewInitializer` takes two
  more parameters, the divider drawable and its size. It is `protected` and
  an implementation detail, but a subclass that overrides it — the pattern
  the in-tree test views use to reach the gesture listener — has to take the
  two new parameters and pass them on.

- The `GridPatternView` sample screen and the wrapping-height `GridView`
  sample screen draw a divider, so the feature is visible in the app. A
  colour has no intrinsic size, so both declare an explicit
  `parchment_dividerSize`.
- The sample's four fixed screens are replaced by a playground that reaches
  every attribute and every combination of them. The first page lists the
  high-level items: the three views, and the behaviours that cut across them
  (paging, snapping, circular scrolling, dividers, selection, the scroll
  listener). Each opens the playground pre-filled with a preset that shows
  that item off — the four old screens are four of the presets — where every
  `parchment_*` attribute is a control, plus the `GridPatternView` pattern.
  Every control carries a hint saying what its attribute does, and one whose
  attribute would have no effect under the other settings disables itself
  and says what it needs; the printed XML leaves such an attribute out too.
  Show inflates the configured view with a status line of what
  `setOnScrollListener`, `OnItemSelectedListener` and `OnItemClickListener`
  report, and an info button shows the layout that reproduces it, which Copy
  puts on the clipboard. Parchment reads its attributes only when a view is
  inflated and has no setters for them, so the playground layouts reference
  `?attr/playground_*` theme attributes and `PlaygroundTheme` applies one
  overlay style per chosen option to the theme it inflates under. The README
  had the `GridPatternItemDefinition` parameters as
  `(left, top, width, height)`; the constructor takes
  `(top, left, height, width)`, and its example, which tiles either way, is
  unchanged.
- **Breaking:** the `AdapterAnimator` and `ChildTouchGestureListener`
  constructors take a `ScrollListenerDispatcher`. Both classes are
  implementation details that the views build for themselves — the in-tree
  test views reach the gesture listener by overriding
  `createAdapterViewInitializer`, not by constructing one — so nothing outside
  the library should be calling these constructors. The animator needs the
  dispatcher because it owns the state machine: a state change that reaches no
  frame, as when a drag is released exactly on the snap position and no
  animation follows, would otherwise never be reported, and asking the
  dispatcher there is what keeps a view with no listener from scheduling a
  frame it does not need.

- `GridView` rows report their bounds without allocating. `Group.getTop`,
  `getBottom`, `getLeft` and `getRight` walked their views with an iterator
  and a boxed `Integer` accumulator; they now index the list and accumulate
  an `int`, because the divider pass reads a cell's bounds on every frame
  while the view moves. An empty group reported those bounds by throwing a
  `NullPointerException` and now reports 0, matching `getMeasuredWidth` and
  `getMeasuredHeight`, which already did.

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
- Sample: Picasso 2.8 (was 2.71828). Same okhttp, but it depends on
  `androidx.exifinterface` instead of `com.android.support:exifinterface` and
  `support-annotations`, which were the last `com.android.support` artifacts
  in the build.

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

- Releases are published to Maven Central by pushing a version tag. The
  `release.yml` workflow checks the tag against `VERSION_NAME`, runs the
  fast CI gates, signs and uploads the AAR with its sources and Javadoc
  through the Central Portal, and creates a GitHub Release whose notes are
  this file's section for that version, with the AAR attached.
- `setOnScrollListener` reports scrolling on all three views, which had no way
  to observe it at all (#23, #19). `OnScrollListener` takes the shape of
  `RecyclerView.OnScrollListener` rather than `AbsListView.OnScrollListener`:
  `onScrollStateChanged` with a `ScrollState` of `idle`, `dragging` or
  `settling`, and `onScrolled` with this frame's movement, not the first visible
  item and a visible count. The platform `AdapterView` declares no
  `setOnScrollListener` of its own, so nothing is overridden or shadowed.
  `settling` covers everything the content does on its own — a fling, a snap, a
  tap-to-snap and a programmatic move are one perceived motion, and Parchment
  hands a fling off to a snap without stopping in between, so splitting them
  would report a state the user never sees. A state is reported only when it
  changes, so a drag, release, snap and rest gives `dragging`, `settling`,
  `idle` once each, and a gesture inside the touch slop reports nothing.
  Displacement is a single signed value along the scroll axis rather than a
  `(dx, dy)` pair with one half always zero, because Parchment scrolls one axis
  at a time; it is the displacement applied to the content, so its sign is the
  opposite of `RecyclerView.onScrolled`'s, and the parameter is named
  `displacement` rather than `dx` to say so. It is what the cells actually
  moved, so a fling clamped at an end reports only the part that landed, a frame
  that moves nothing reports nothing, and a jump — `setSelection`, a data set
  change — reports no displacement because it is not a scroll. Both callbacks
  run after the frame's layout, and `onScrolled` runs before the state change,
  so `idle` means every movement has already been reported. What is reported is
  a change and not a snapshot: a listener set while the content is already
  moving is told the current state on the next frame when it differs from the
  last state reported on that view, which is what keeps setting the same
  listener twice mid-gesture from repeating `dragging`. Setting a listener adds
  no animation frames to a gesture, and the callback path allocates nothing: the
  dispatcher reuses its two fields and the states are enum constants.

- `parchment_divider` and `parchment_dividerSize` draw a divider in all
  three views, the thing the platform `ListView` has and Parchment did not
  (#25). A divider falls on every edge internal to the content and on none
  at the content's outer boundary. Each drawn item is asked about its
  trailing edge along each axis: where another item lies across the gap a
  divider is painted there, spanning exactly the run the two items share
  along the other axis, and an item with no neighbour on a side is at the
  boundary and gets nothing. In a `ListView` that is one divider between
  each pair of items and one fewer than there are cells on screen. In
  `GridView` and `GridPatternView`, where a cell is a whole group, it is
  also a divider between the items stacked inside one group, so a
  `GridPatternView` pattern of mixed spans and T-junctions is divided
  without rows or columns having to exist. Every shared edge is painted
  once. The divider is decoration and takes no space of its own: it is
  centred in the `parchment_cellSpacing` gap, so the spacing is what you
  size to make room for it, and a divider thicker than the spacing overflows
  evenly onto both items and is painted over them — which is also what makes
  one visible when the spacing is zero. Two items that overlap, as a negative
  `parchment_cellSpacing` lays them, have no gap and get no divider. Where a
  gap between rows crosses a gap between columns nothing abuts either gap, so
  the crossing is left unpainted. A divider begins and ends where the items it
  separates do, so
  it stays inside `android:padding*` with them; `android:clipToPadding` is
  applied to the cells inside `ViewGroup.dispatchDraw` and restored before
  it returns, so it never reaches the divider under either setting. Without
  `parchment_dividerSize` the drawable's intrinsic size along the scroll
  axis is used, and a colour has none, so a colour divider with no size
  paints nothing, as with the platform widget. The divider is drawn as it is
  given: drawable state and animation are not driven, as with the platform
  widget.
- An instrumented harness, `ParchmentViewHarness`, that inflates a view
  from a layout with a real `LayoutInflater`, attaches it to an Activity at
  an exact pixel size, drives real measure and layout passes, dispatches
  real gestures, and hands a test an immutable snapshot of where the
  children landed or of the pixels it painted. All fourteen `parchment_`
  attributes are covered by on-device tests built on it, asserting geometry
  and painted pixels rather than getters, as is paging in both modes: a real fling and a real slow drag each page by the
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
