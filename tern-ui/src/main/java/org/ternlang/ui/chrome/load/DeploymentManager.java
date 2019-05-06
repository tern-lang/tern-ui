package org.ternlang.ui.chrome.load;

import org.ternlang.ui.OperatingSystem;

import java.io.File;

public class DeploymentManager {

   public static DeploymentTask deploy(String deployPath, String mainClass, String[] arguments, String... properties) {
       boolean alreadyDeployed = LibraryLoader.isLibraryDeployed(deployPath);
       OperatingSystem os = OperatingSystem.resolveSystem();
       File installFolder = LibraryLoader.installPath(deployPath);
       String[] libraryPaths = LibraryLoader.loadFrom(deployPath);
       Runnable mainTask = () -> ApplicationLauncher.launch(mainClass, arguments, installFolder, libraryPaths, properties);

       return new DeploymentTask(os, mainTask, alreadyDeployed);
   }
}