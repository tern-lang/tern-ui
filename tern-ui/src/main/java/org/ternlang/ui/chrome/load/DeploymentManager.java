package org.ternlang.ui.chrome.load;

public class DeploymentManager {

   public static DeploymentTask deploy(String deployPath, Class mainClass, String[] arguments, String... properties) {
       boolean alreadyDeployed = LibraryLoader.isLibraryDeployed(deployPath);
       String[] libraryPaths = LibraryLoader.loadFrom(deployPath);
       Runnable mainTask = () -> ApplicationLauncher.launch(mainClass, arguments, libraryPaths, properties);

       return new DeploymentTask(mainTask, alreadyDeployed);
   }
}