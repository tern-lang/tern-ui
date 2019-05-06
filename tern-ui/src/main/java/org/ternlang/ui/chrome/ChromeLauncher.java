package org.ternlang.ui.chrome;

import org.ternlang.ui.chrome.load.DeploymentManager;
import org.ternlang.ui.chrome.load.DeploymentTask;

import static org.ternlang.ui.chrome.ChromeBrowser.LIBRARY_PATH;

public class ChromeLauncher {

    public static void main(String[] args) throws Exception {
        String mainClass = ChromeBrowser.class.getName();
        DeploymentTask task = DeploymentManager.deploy(LIBRARY_PATH, mainClass, args);
        Runnable forkTask = task.getForlTask();

        forkTask.run();
    }

}
