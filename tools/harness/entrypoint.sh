#!/usr/bin/env bash
# Keep an adb server running for the container's lifetime. `-a` makes it
# listen on all container interfaces so the published port reaches it.
set -u
while true; do
  adb -a nodaemon server start
  echo "harness: adb server exited ($?); restarting in 2s" >&2
  sleep 2
done
