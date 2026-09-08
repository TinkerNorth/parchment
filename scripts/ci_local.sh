#!/usr/bin/env bash
# Runs every gate Android CI runs, in the same order, against the local
# tree. Mirrors .github/workflows/android-ci.yml so a green run here means
# a green run there. Instrumented tests need a device: they run when one is
# attached and are skipped with a notice otherwise so the fast gates stay
# usable offline.
#
#   scripts/ci_local.sh                     fast gates + instrumented tests if a device is attached
#   scripts/ci_local.sh --no-instrumented   fast gates only
set -euo pipefail
cd "$(dirname "$0")/.."

INSTRUMENTED=1
for arg in "$@"; do
  case "$arg" in
    --no-instrumented) INSTRUMENTED=0 ;;
    *) echo "unknown flag: $arg" >&2; exit 2 ;;
  esac
done

if [ -z "${JAVA_HOME:-}" ]; then
  echo "JAVA_HOME is not set." >&2
  echo "Any JDK 17+ works; gradle/gradle-daemon-jvm.properties provisions the build JVM itself." >&2
  echo "  export JAVA_HOME=\"C:/Program Files/Android/Android Studio/jbr\"   # Windows example" >&2
  exit 1
fi

# The POSIX wrapper runs under Git Bash on Windows too, so one path covers
# every OS.
GRADLE="./gradlew"
GRADLE_ARGS="--console=plain"

step() { echo ""; echo "=== $1 ==="; }

step "Spotless (google-java-format, license headers)"
$GRADLE spotlessCheck $GRADLE_ARGS

step "Android lint (library + sample, warnings are errors)"
$GRADLE :library:lintDebug :sample:lintDebug $GRADLE_ARGS

step "JVM unit tests (Robolectric, debug + release variants)"
$GRADLE :library:test $GRADLE_ARGS

step "Assemble library AAR + sample debug APK"
$GRADLE :library:assembleRelease :sample:assembleDebug $GRADLE_ARGS

if [ "$INSTRUMENTED" -eq 0 ]; then
  echo ""; echo "=== instrumented tests skipped (--no-instrumented) ==="
  echo "All non-instrumented gates passed."
  exit 0
fi

ADB="adb"
command -v adb >/dev/null 2>&1 || {
  for root in "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}" "${LOCALAPPDATA:-}/Android/Sdk"; do
    [ -n "$root" ] && [ -x "$root/platform-tools/adb" ] && ADB="$root/platform-tools/adb" && break
    [ -n "$root" ] && [ -x "$root/platform-tools/adb.exe" ] && ADB="$root/platform-tools/adb.exe" && break
  done
}
DEVICES=$("$ADB" devices 2>/dev/null | grep -c -w device || true)
if [ "${DEVICES:-0}" -ge 1 ]; then
  step "Instrumented tests (attached device)"
  $GRADLE :library:connectedDebugAndroidTest $GRADLE_ARGS
else
  echo ""; echo "=== instrumented tests skipped (no device) ==="
  echo "Attach a device or emulator to run library/src/androidTest."
fi

echo ""; echo "All requested gates passed."
