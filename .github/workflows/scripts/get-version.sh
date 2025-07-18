#!/bin/bash
# Usage: ./get-version.sh systest

ENV=$1
FILE=VERSION

TAG=$(grep "^\[$ENV\]" -A 2 $FILE | grep tag= | cut -d'=' -f2 | tr -d '[:space:]')
DATE=$(date +'%Y%m%d%H%M')
PR_NUMBER=$(git log -1 --pretty=%B | grep -oE '#[0-9]+' | head -n1 | tr -d '#' || echo manual)
SHORT_SHA=$(git rev-parse --short HEAD)

FULL_TAG="$TAG-$DATE-pr$PR_NUMBER-$SHORT_SHA"

echo "VERSION_TAG=$FULL_TAG"
echo "VERSION_TAG=$FULL_TAG" >> $GITHUB_OUTPUT
