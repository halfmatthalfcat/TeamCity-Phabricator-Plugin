package com.couchmate.teamcity.phabricator.tasks;

/**
 * Created by mjo20 on 10/11/2015.
 */
public abstract class Task {

    public enum Result{
        SUCCESS,
        FAILURE,
        IGNORED,
        SKIPPED,
        UNKNOWN
    }

    protected Result result = Result.UNKNOWN;

    protected abstract void setup();
    protected abstract void execute();
    protected abstract void teardown();

    public Result run(){
        setup();
        execute();
        teardown();

        return result;
    }

}
