# Custom Shields (customshields) 2.7.0

Fabric client mod for Minecraft 1.21.11: Wind Charge Auto-Hit with a dark settings menu.

## Open in IntelliJ IDEA
1. Unzip, then File > Open and pick the `customshields` folder (the one containing build.gradle).
2. Click "Trust Project" and wait for the Gradle import to finish (first time takes a few minutes).
3. File > Project Structure > Project: set the SDK to a JDK (21 recommended; 25 also works for building).
   Settings > Build, Execution, Deployment > Build Tools > Gradle: set "Gradle JVM" to the same JDK.
4. Open the Gradle tool window (elephant icon, right side): Tasks > build > build (double-click).
5. The finished mod is build/libs/customshields-2.7.0.jar  (ignore the -sources jar).

To test inside a dev game: Gradle tool window > Tasks > fabric > runClient.

## Install
Put customshields-2.7.0.jar and Fabric API 0.141.3+1.21.11 in .minecraft/mods
(Fabric Loader 0.18.4 or newer).

## Controls
Right Shift = open menu, G = toggle auto-hit (both rebindable in the menu).
Settings: .minecraft/config/customshields.json
