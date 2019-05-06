package org.ternlang.ui.chrome;

import org.ternlang.ui.chrome.load.DeploymentManager;

import static org.ternlang.ui.chrome.ChromeBrowser.LIBRARY_PATH;

public class ChromeLauncher {

    public static void main(String[] args) throws Exception {
        DeploymentManager.deploy(LIBRARY_PATH, ChromeBrowser.class, args);
    }

}
