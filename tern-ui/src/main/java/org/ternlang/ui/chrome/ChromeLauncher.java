package org.ternlang.ui.chrome;

import org.ternlang.ui.chrome.install.deploy.DeploymentManager;
import org.ternlang.ui.chrome.install.deploy.DeploymentTask;

public class ChromeLauncher {

    public static final String LIBRARY_PATH = ".cef";

    public static void main(String[] args) throws Exception {
        String mainClass = ChromeBrowser.class.getName();
        DeploymentTask task = DeploymentManager.deploy(LIBRARY_PATH, mainClass, args);
        Runnable forkTask = task.getForkTask();

        forkTask.run();
    }

}
