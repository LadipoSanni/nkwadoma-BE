#!/bin/bash
# Usage: ./bump-version.sh prod patch

ENV=$1
TYPE=$2  # patch, minor, major

TAG_LINE=$(grep "^\[$ENV\]" -A 2 VERSION | grep tag=)
CURRENT_TAG=$(echo "$TAG_LINE" | cut -d= -f2 | tr -d 'v')

IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_TAG"

case $TYPE in
  major)
    ((MAJOR++)); MINOR=0; PATCH=0;;
  minor)
    ((MINOR++)); PATCH=0;;
  patch)
    ((PATCH++));;
esac

NEW_TAG="v$MAJOR.$MINOR.$PATCH"
sed -i "s|^\[$ENV\][^[]*tag=v.*|[${ENV}]\nenv=${ENV}\ntag=${NEW_TAG}|" VERSION

echo "✅ Bumped $ENV version to $NEW_TAG"
