package org.ternlang.ui.chrome.install.deploy;

import org.ternlang.ui.OperatingSystem;
import org.ternlang.ui.chrome.install.Installer;

public class DeploymentManager {

   public static DeploymentTask deploy(String deployPath, String mainClass, String[] arguments, String... properties) {
       OperatingSystem os = OperatingSystem.resolveSystem();
       Installer module = os.getInstaller(deployPath);
       boolean alreadyDeployed = module.isInstalled();
       Runnable mainTask = () -> module.install().launch(mainClass, arguments, properties);

       return new DeploymentTask(os, mainTask, alreadyDeployed);
   }
}