package com.couchmate.teamcity.phabricator;

import com.couchmate.teamcity.phabricator.tasks.ApplyPatch;
import com.couchmate.teamcity.phabricator.conduit.ConduitClient;
import com.couchmate.teamcity.phabricator.tasks.HarbormasterBuildStatus;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Agent extends AgentLifeCycleAdapter {

    private BuildProgressLogger buildLogger = null;
    private PhabLogger logger = null;
    private AppConfig appConfig = null;
    private Collection<AgentBuildFeature> buildFeatures = null;
    private ConduitClient conduitClient = null;
    private String serverUrl = null;
    private boolean first = true;
    private AgentRunningBuild runningBuild = null;

    public Agent(
            @NotNull final EventDispatcher<AgentLifeCycleListener> eventDispatcher,
            @NotNull final PhabLogger phabLogger,
            @NotNull final AppConfig appConfig
    ){
        eventDispatcher.addListener(this);
        this.logger = phabLogger;
        this.appConfig = appConfig;
        this.logger.info("Instantiated");
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        super.buildStarted(runningBuild);
        //Get logger
        this.logger.setBuildLogger(runningBuild.getBuildLogger());
        this.runningBuild = runningBuild;
        this.buildFeatures = this.runningBuild.getBuildFeaturesOfType("phabricator");
    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        super.beforeRunnerStart(runner);

        if(!this.buildFeatures.isEmpty()) {
            try {
                Map<String, String> configs = new HashMap<>();
                configs.putAll(runner.getBuild().getSharedBuildParameters().getEnvironmentVariables());
                configs.putAll(runner.getBuild().getSharedConfigParameters());
                configs.putAll(this.buildFeatures.iterator().next().getParameters());
                this.appConfig.setParams(configs);
                this.appConfig.setLogger(this.logger);
                this.appConfig.parse();
            } catch (Exception e) { this.logger.warn("Build Started Error: ", e); }
        }

        if (this.appConfig.isEnabled()) {
            this.logger.info("getting build id " + runner.getBuild().getBuildId());
            this.logger.info("Plugin is enabled, starting patch process");
            this.appConfig.setWorkingDir(runner.getWorkingDirectory().getPath());
            new ApplyPatch(runner, this.appConfig, this.logger).run();
            this.conduitClient = new ConduitClient(this.appConfig.getPhabricatorUrl(), this.appConfig.getPhabricatorProtocol(), this.appConfig.getConduitToken(), logger);
            this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build started: http://130.211.136.223/viewLog.html?buildId=" + runner.getBuild().getBuildId());
            this.first = false;
        }
        //If plugin enabled, run it
    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
         super.runnerFinished(runner, status);
    }

    @Override
    public void buildFinished(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus status) {
        super.buildFinished(build, status);
        String buildInfo = "http://130.211.136.223/viewLog.html?buildId=" + build.getBuildId();
        if(status.isFailed() && status.isFinished()) {
           this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build failed: " + buildInfo);
        } else if (!status.isFailed() && status.isFinished()) {
            this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build successful: " +buildInfo);
        } else {
            this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build interrupted : " +buildInfo);
        }
    }
}
