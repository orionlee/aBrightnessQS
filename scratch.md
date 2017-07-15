# Scratchpad various pointers and tips

## Useful command lines
  
```
. utils/install_n_logcat.sh

gradleLB.sh :app:installDebug
adb logcat -s BTS:V

agradleLB.sh :app:testDebugUnitTest
gradleLB.sh :app:compileDebugSources
gradleLB.sh :app:assembleDebug
adb install -r app/build/outputs/apk/app-debug.apk


gradleLB.sh :app:compileCompletedDebugJavaWithJavacC

./gradlew tasks
./gradlew :app:assembleDebug
```

## Make java language support (redhat version) somewhat work 
- ensure local.properties *DOES NOT* define sdk.dir
  - it will cause java language server fail in configuring using gradle, which is what I expect
- when open a java file, the tool will prompt you that it's found incomplete classpath, only syntax highlight will work. It is a confirmation that the setup works.
- To facilitate completion of main android api, edit java language server's classpath 
  to add android.jar (platform lib).
  - JLS's file is located at:  `%USERPROFILE\AppData\Roaming\Code\User\workspaceStorage\<some-long-id>\redhat.java\jdt_ws\jdt.ls-java-project\`
  - edit .classpath and add the following line:
    `	<classpathentry kind="lib" path="libsManual/android.jar"/> <!-- manually added -->`
  - `mkdir libsManual` there, and copy sdk's android.jar there, e.g.
    `cp /c/Android/sdk/platforms/android-24/android.jar libsManual/android-24.jar`


## Others
- The tool to generate launcher icons: 
https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=clipart&foreground.clipart=brightness_6&foreground.space.trim=1&foreground.space.pad=0.15&foreColor=rgb(0%2C%20255%2C%200)&backColor=rgb(158%2C%20158%2C%20158)&crop=0&backgroundShape=square&effects=elevate&name=ic_launcher_brightness6_green

- Similar tools: https://materialdesignicons.com/icon/brightness-6
- SVG editor (to edit icons): http://draw-svg.appspot.com/drawsvg.html
