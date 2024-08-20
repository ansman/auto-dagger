#!/usr/bin/env bash

./gradlew --continue -q :compiler:cleanProcessTestResources :compiler:test -PupdateExpectedTestFiles=true