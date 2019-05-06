package org.ternlang.ui.chrome.load;

public class DeploymentManager {

   public static void deploy(String deployPath, Class mainClass, String[] arguments) {
     String[] libraryPaths = LibraryLoader.loadFrom(deployPath);
     ApplicationLauncher.launch(mainClass, arguments, libraryPaths);
   }
}