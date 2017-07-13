# Scratchpad various pointers and tips

## Useful command lines
  
```
. utils/install_n_logcat.sh

gradleLB.sh :app:installDebug
adb logcat -s BTS:V

gradleLB.sh :app:compileDebugSources
gradleLB.sh :app:assembleDebug
adb install -r app/build/outputs/apk/app-debug.apk


gradleLB.sh :app:compileCompletedDebugJavaWithJavacC

./gradlew tasks
./gradlew :app:assembleDebug
```

## Others
- The tool to generate launcher icons: 
https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=clipart&foreground.clipart=brightness_6&foreground.space.trim=1&foreground.space.pad=0.15&foreColor=rgb(0%2C%20255%2C%200)&backColor=rgb(158%2C%20158%2C%20158)&crop=0&backgroundShape=square&effects=elevate&name=ic_launcher_brightness6_green

- Similar tools: https://materialdesignicons.com/icon/brightness-6
- SVG editor (to edit icons): http://draw-svg.appspot.com/drawsvg.html
