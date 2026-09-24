#!/bin/bash
mkdir -p web
cp -u app/build/outputs/apk/debug/app-debug.apk web/app-debug.apk 2>/dev/null || true
while true; do
  node server.js
  sleep 1
done
