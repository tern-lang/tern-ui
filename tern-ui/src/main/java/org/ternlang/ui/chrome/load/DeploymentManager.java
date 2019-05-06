package org.ternlang.ui.chrome.load;

public class DeploymentManager {

   public static boolean deploymentDone(String libraryPath, Class mainClass, String[] arguments) {
      if (!LibraryLoader.isLibraryLoaded(libraryPath)) {
         String[] libraryPaths = LibraryLoader.loadFrom(libraryPath);
         ApplicationLauncher.launch(mainClass, arguments, libraryPaths);
         return false;
     }
      return true;
   }
}