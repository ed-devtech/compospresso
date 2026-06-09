#!/usr/bin/env bash
set -euo pipefail

SRC="/Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/doppio"
DST="$(cd "$(dirname "$0")/.." && pwd)/library/src/main/java/dev/eddev/compospresso"

mkdir -p "$DST"

# Copy directory tree, skipping doppioutils/ (replaced by internal/) and README.md (handled separately).
cd "$SRC"
find . -type f -name '*.kt' ! -path './doppioutils/*' | while read -r f; do
    target="$DST/${f#./}"
    mkdir -p "$(dirname "$target")"
    cp "$f" "$target"
done

# Rewrite in place. Order matters: longer FQNs first so we don't accidentally rewrite the doppioutils FQN.
find "$DST" -type f -name '*.kt' -print0 | xargs -0 sed -i '' \
    -e 's|com\.life360\.android\.doppio\.doppioutils\.DoppioError|dev.eddev.compospresso.internal.CompospressoError|g' \
    -e 's|com\.life360\.android\.testsupport\.TestError|dev.eddev.compospresso.internal.CompospressoError|g' \
    -e 's|com\.life360\.android\.testsupport\.TestLog|dev.eddev.compospresso.internal.CompospressoLog|g' \
    -e 's|com\.life360\.android\.testsupport\.TAG|dev.eddev.compospresso.internal.TAG|g' \
    -e 's|com\.life360\.android\.doppio|dev.eddev.compospresso|g' \
    -e 's|DoppioError(|CompospressoError(|g' \
    -e 's|TestError(|CompospressoError(|g' \
    -e 's|TestLog\.|CompospressoLog.|g'

# Rename the typo: AssertHelpter.kt -> AssertHelper.kt
if [ -f "$DST/assertion/AssertHelpter.kt" ]; then
    mv "$DST/assertion/AssertHelpter.kt" "$DST/assertion/AssertHelper.kt"
fi

echo "Extraction complete. Copied $(find "$DST" -name '*.kt' | wc -l | tr -d ' ') Kotlin files."
