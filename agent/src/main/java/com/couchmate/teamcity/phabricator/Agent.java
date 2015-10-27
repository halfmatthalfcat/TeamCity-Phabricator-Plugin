package com.couchmate.teamcity.phabricator;

import com.couchmate.teamcity.phabricator.tasks.ApplyPatch;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent extends AgentLifeCycleAdapter {

    private Boolean enabled = false;
    private BuildProgressLogger logger = null;
    private AppConfig appConfig = null;


    public Agent(@NotNull final EventDispatcher<AgentLifeCycleListener> eventDispatcher){
        eventDispatcher.addListener(this);
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        super.buildStarted(runningBuild);
        //Get logger
        this.logger = runningBuild.getBuildLogger();
    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        super.beforeRunnerStart(runner);
        //If plugin enabled, run it
        Map<String, String> configs = new HashMap<>();
        configs.putAll(runner.getBuildParameters().getEnvironmentVariables());
        configs.putAll(runner.getConfigParameters());

        appConfig = new AppConfig(configs);

        if(appConfig.isEnabled()){
            logger.message("Phabricator Plugin: Plugin is enabled, starting patch process");
            appConfig.setWorkingDir(runner.getWorkingDirectory().getPath());

            new ApplyPatch(appConfig, logger).run();

        } else {
            logger.message("Phabricator Plugin: Plugin is disabled.");
        }

    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        super.runnerFinished(runner, status);
        //Report back status
    }

}
