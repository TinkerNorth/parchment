# Contributing to Parchment

Thanks for contributing. This document captures the conventions that are
not obvious from reading the code: the style gates, the layout-engine
rules, and what CI enforces.

## Code of conduct

This project follows the [Contributor Covenant](CODE_OF_CONDUCT.md).
By participating, you agree to uphold it. Report unacceptable behavior
to `security@tinkernorth.com`.

## Getting set up

```bash
# 1) Install Android Studio Otter (2025.2)+ for AGP 9.4 and SDK 37; any JDK 17+ runs Gradle
# 2) Open the project in Android Studio (Gradle sync downloads deps and the JDK 21 daemon)
# 3) Point git at the in-tree pre-commit hook
scripts/setup-hooks.sh
```

The pre-commit hook runs `google-java-format --aosp -i` (autofix, re-stages)
on staged Java files when the binary is installed, and always checks that
each staged Java file starts with the SPDX header. CI runs
`./gradlew spotlessCheck`, Android Lint, and the test suites, so anything
that slips locally fails the PR. `scripts/ci_local.sh` runs the same gates
in the same order.

## License headers

Every Java source file starts with:

```
// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.
```

`./gradlew spotlessApply` adds it (the template is
`config/spotless/license-header.java`). Don't introduce code under a
different license: the project is Apache-2.0 end-to-end.

## Style

### Java

- google-java-format in AOSP mode (4-space indent, 100 columns). Spotless is
  authoritative: `./gradlew spotlessApply` to fix, `spotlessCheck` to verify.
- `javac` runs with `-Xlint:all -Werror`. Fix the warning rather than
  suppressing it; when a suppression is the honest answer (an unchecked
  cast the type system cannot express), scope it to the smallest element
  and say why in a comment.
- Android Lint runs with `warningsAsErrors` and no baseline. A lint issue
  that is genuinely inapplicable is silenced in the module's `lint.xml` with
  a comment explaining what makes it inapplicable.
- The library has no runtime dependencies and must stay that way.
  `androidx.annotation` is `compileOnly`.
- Prefer a test to a comment. Behaviour that needs explaining gets a test
  named for the behaviour; a comment is the last resort for a constraint
  that cannot be tested (a platform quirk, a build-tool limitation), states
  why in one or two lines, and never narrates what the next line does.
- Public API is the three custom views, their XML attributes, and the
  `GridPatternItemDefinition` types. Anything else in `mobi.parchment.widget.adapterview`
  is an implementation detail even when it is `public` for historical
  reasons; don't widen it without a reason.

### Shape of the code

These rules are older than the tooling and are enforced in review, not by
a gate.

- **As immutable and as static as possible.** Every field, local, and
  parameter is `final` unless it must change; a field that must change is
  the exception that needs a reason. Nested classes are `static` unless
  they need the outer instance, and a class that has no reason to be
  extended is `final`. Values that never change are `static final`
  constants with a name, not literals in the middle of a method.
- **Member variables start with `m`.** `mOffset`, `mCells`,
  `mScrollAnimator`. Constants are `UPPER_SNAKE_CASE`; parameters and locals
  have no prefix. This is how you tell, at the point of use, whether a value
  is state or scratch.
- **No anonymous methods.** No anonymous inner classes and no lambdas, in
  library, sample, or test code. A callback is a named class (usually a
  `private static final` nested class that takes what it needs through its
  constructor), so it can be found, tested, and read on its own.
  `AnimationFrameRunnable` in `AbstractAdapterView` is the pattern.
- **Split values into simple, named steps.** One operation per line, with
  the result in a `final` local named for what it is, even when that looks
  verbose:

  ```java
  final int cellSize = getCellSize(cell);
  final int cellEnd = cellStart + cellSize;
  final boolean cellIsOffScreenBehind = cellEnd < 0;
  ```

  not `if (cellStart + getCellSize(cell) < 0)`. The names are the
  documentation; the debugger can show each value; and a test can pin each
  step.

### Layout engine

`LayoutManager` and its subclasses are the hot path: they run on every
scroll frame.

- No allocation per frame. Cells and view lists are reused; look at
  `AdapterViewManager` before adding a collection.
- Everything orientation-specific goes through `ScrollDirectionManager`.
  If you find yourself writing `getLeft()` or `getTop()` in a layout
  manager, you are about to break the other orientation.
- Snap behaviour lives in `snapposition/`. A new snap mode is a new
  `SnapPositionInterface` implementation plus an enum value, not a branch
  in `LayoutManager`.
- Circular scrolling wraps positions in the layout engine. Never leak
  wrapped positions to the adapter.

## Tests

- `library/src/test`: Robolectric unit tests for the layout managers, snap
  positions, recycler, and the inflated `ListView`. They run on the JDK 21
  daemon against SDK 36 (`library/src/test/resources/robolectric.properties`;
  Robolectric 4.16 does not emulate SDK 37 yet).
- `library/src/androidTest`: instrumented smoke tests that inflate a view
  from XML and lay it out on a real framework. CI runs them on an API 35
  emulator; locally, `scripts/ci_local.sh` runs them when a device is
  attached.
- A layout-engine change needs a unit test in the matching
  `*LayoutManagerTest`; a change to attribute parsing or inflation needs
  coverage in `HorizontalListViewTest` or the instrumented test.

### Test-driven, every flow

- **Write the test first and watch it fail.** A fix starts with a test that
  reproduces the bug on the current code; a feature starts with a test that
  describes the behaviour. Red, then green. Say in the PR which tests failed
  before the change; a test that never failed has not proven anything.
- **Cover every flow, not every line.** Line coverage is not the target.
  Each branch of each new condition, each early return, each end of a clamp
  or a loop, and each state a state machine can be in when the new code runs
  gets its own case, named for the behaviour
  (`centerSnap_lastCellPulledPastTheCenter_isHeldAtTheCenter`). If a branch
  has no test, either it is dead and goes, or it needs one.
- **Assume nothing; validate with a test.** A claim about how the framework
  behaves (`Scroller.setFinalX` after a fling, what `measure` skips, what a
  posted runnable does on a detached view) or about what an existing method
  returns is confirmed by a test, not by reading a comment or a docstring.
  Keep the test if it pins a dependency; delete it if it was only a probe.
- **Test at the level where the behaviour lives.** Layout maths in the
  `*LayoutManagerTest` harnesses (a `LinearLayout` implementing
  `AdapterViewHandler` and a named adapter class), animator state in
  `AdapterAnimatorTest`, frame scheduling on an attached view in
  `AnimationFrameSchedulingTest`, and anything that depends on the real
  framework in `library/src/androidTest`.
- **Tests follow the same shape rules as the code.** Named nested classes
  instead of lambdas, `final` locals, one asserted step at a time.

Windows note: Robolectric cannot load its native runtime when the home
directory contains a space. The library build detects that and points the
test JVM at a gitignored `.robolectric/` cache in the repo root; nothing to
configure.

## Branching & PRs

- All changes land on `main` via pull request: no direct pushes.
- Use the PR template (`.github/pull_request_template.md`) to describe the
  change and the tests you ran. Call out anything that changes an XML
  attribute or a public method signature: that is a breaking change for
  every consumer's layouts.
- Keep commits focused; squash noisy fixup commits before review.

## What CI runs

Build + style:

- `android-ci.yml`: `./gradlew spotlessCheck`, `./gradlew :library:lintDebug :sample:lintDebug`,
  `./gradlew :library:test`, `./gradlew :library:assembleRelease :sample:assembleDebug`
  (uploads the AAR and sample APK as artifacts), then
  `:library:connectedDebugAndroidTest` on an emulator.

Security gates (also blocking):

- `security.yml`: action-pin lint, vulnerability allowlist expiry,
  OSV-Scanner, gitleaks secret scan, and GitHub `dependency-review-action`.
- `codeql.yml`: CodeQL `java-kotlin` analysis (security-extended +
  security-and-quality query packs).

If any step fails, the PR is blocked.

## Security

### Adding a vulnerability allowlist entry

Open a PR that adds an entry to [`.security/allowlist.yaml`](.security/allowlist.yaml)
(see the schema in the file). Required fields: `cve`, `reason`, `owner`,
`expires`. CI rejects the PR if any field is missing or `expires` is in
the past.

### Running security checks locally

```bash
# Action-pin lint
grep -REn '^\s*uses:' .github/workflows/ \
  | grep -vE '@[0-9a-f]{40}\b' \
  || echo "all pinned"

# Allowlist expiry
python3 - <<'PY'
import datetime, yaml, sys
data = yaml.safe_load(open('.security/allowlist.yaml').read()) or {}
for e in data.get('exceptions', []) or []:
    if datetime.date.fromisoformat(str(e['expires'])) < datetime.date.today():
        print('EXPIRED:', e); sys.exit(1)
PY

# OSV-Scanner
osv-scanner --recursive --skip-git .

# Gitleaks
gitleaks detect --no-banner --redact --source .
```

### Gradle dependency verification (`gradle/verification-metadata.xml`)

The file is not committed yet. When you intentionally add or upgrade a
dependency, you can regenerate verification metadata so transitive jar
tampering fails resolution, and diff it as a review aid:

```bash
./gradlew --write-verification-metadata sha256 \
  :library:assembleRelease :sample:assembleDebug :library:test
```

## Reporting bugs

Use the issue templates under `.github/ISSUE_TEMPLATE/`. Include the XML
for the Parchment view, what the adapter returns, the Android version, and
a stack trace or screen recording. A failing `*LayoutManagerTest` case is
the fastest way to get a layout bug fixed.
