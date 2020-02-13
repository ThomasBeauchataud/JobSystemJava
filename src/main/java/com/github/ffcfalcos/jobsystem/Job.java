package com.github.ffcfalcos.jobsystem;

public abstract class Job {

    public void execute() {
        handle();
        onHandled();
    }

    protected abstract void handle();

    protected abstract void onHandled();

}
