#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")/.."

git config core.hooksPath .githooks
chmod +x .githooks/*

echo "✓ core.hooksPath → .githooks"
echo
echo "Recommended tooling (install once):"
echo "  macOS:    brew install google-java-format"
echo "  Linux:    download the google-java-format release jar and wrap it in a script on PATH"
echo "  Windows:  scoop install google-java-format   (or the release jar as above)"
echo
echo "Formatting is authoritative in Gradle: './gradlew spotlessApply' fixes, and CI runs"
echo "'./gradlew spotlessCheck'. The pre-commit hook only formats staged Java files when the"
echo "google-java-format binary is present, and always checks for the SPDX header."
