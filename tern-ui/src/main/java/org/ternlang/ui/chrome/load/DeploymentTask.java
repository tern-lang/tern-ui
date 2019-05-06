package org.ternlang.ui.chrome.load;

import org.ternlang.ui.OperatingSystem;

public class DeploymentTask {

    private final OperatingSystem os;
    private final Runnable forkTask;
    private final boolean alreadyDeployed;

    public DeploymentTask(OperatingSystem os, Runnable forkTask, boolean alreadyDeployed) {
        this.alreadyDeployed = alreadyDeployed;
        this.forkTask = forkTask;
        this.os = os;
    }

    public OperatingSystem getOperatingSystem() {
        return os;
    }

    public Runnable getForkTask() {
        return forkTask;
    }

    public boolean isAlreadyDeployed(){
        return alreadyDeployed;
    }
}
