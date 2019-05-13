package org.ternlang.ui.chrome.install.deploy;

import org.ternlang.ui.OperatingSystem;
import org.ternlang.ui.chrome.install.Installer;
import org.ternlang.ui.chrome.install.Launcher;

public class DeploymentManager {

   public static DeploymentTask deploy(String deployPath, String mainClass, String[] arguments, String... properties) {
       OperatingSystem os = OperatingSystem.resolveSystem();
       Installer module = os.getInstaller(deployPath);
       boolean alreadyDeployed = module.isInstalled();
       Launcher launcher = module.install();
       Runnable mainTask = () -> launcher.launch(mainClass, arguments, properties);

       return new DeploymentTask(os, mainTask, alreadyDeployed);
   }
}