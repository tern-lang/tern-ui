package org.ternlang.ui.chrome.load;

import java.io.File;

public class DeploymentManager {

   public static DeploymentTask deploy(String deployPath, String mainClass, String[] arguments, String... properties) {
       boolean alreadyDeployed = LibraryLoader.isLibraryDeployed(deployPath);
       File installFolder = LibraryLoader.installPath(deployPath);
       String[] libraryPaths = LibraryLoader.loadFrom(deployPath);
       Runnable mainTask = () -> ApplicationLauncher.launch(mainClass, arguments, installFolder, libraryPaths, properties);

       return new DeploymentTask(mainTask, alreadyDeployed);
   }
}