#!/bin/sh

# Automate typical flow of
# 1. generating and install app (debug)
# 2. run logcat to monitor output

gradleLB.sh :app:installDebug

if [ $? = 0 ]; then
  echo Running: 
  echo adb logcat -s BTS:V
  adb logcat -s BTS:V
else
  echo 
fi
