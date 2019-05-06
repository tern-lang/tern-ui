package org.ternlang.ui.chrome.load;

public class DeploymentTask {

    private final Runnable forkTask;
    private final boolean alreadyDeployed;

    public DeploymentTask(Runnable forkTask, boolean alreadyDeployed) {
        this.alreadyDeployed = alreadyDeployed;
        this.forkTask = forkTask;
    }

    public Runnable getForlTask() {
        return forkTask;
    }

    public boolean isAlreadyDeployed(){
        return alreadyDeployed;
    }
}
