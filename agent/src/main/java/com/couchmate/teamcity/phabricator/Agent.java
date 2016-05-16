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
    private Map<String, Integer> unique;
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
        // temporarily set this statically this.serverUrl = runningBuild.getAgentConfiguration().getServerUrl();
        this.serverUrl = "http://teamcity.devops.wepay-inc.com/"; 
        this.unique = new HashMap<String, Integer>();

    }

    private void refreshConfig(AgentRunningBuild build) {
        this.buildFeatures = build.getBuildFeaturesOfType("phabricator");
        this.appConfig.setParams(null);
        this.appConfig.setEnabled(false);
        if(!this.buildFeatures.isEmpty()) {
            try {
                Map<String, String> configs = new HashMap<>();
                configs.putAll(build.getSharedBuildParameters().getEnvironmentVariables());
                configs.putAll(build.getSharedConfigParameters());
                configs.putAll(this.buildFeatures.iterator().next().getParameters());
                this.appConfig.setParams(configs);
                this.appConfig.setLogger(this.logger);
                this.appConfig.parse();
                int count = this.unique.containsKey(this.appConfig.getHarbormasterTargetPHID()) ? this.unique.get(this.appConfig.getHarbormasterTargetPHID()) : 0;
                this.unique.put(this.appConfig.getHarbormasterTargetPHID(), count + 1);
              } catch (Exception e) { this.logger.warn("Build Started Error: ", e); }
         }
    }
    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        super.beforeRunnerStart(runner);
        this.refreshConfig(runner.getBuild());
        if (this.appConfig.isEnabled() && this.unique.get(this.appConfig.getHarbormasterTargetPHID()) == 1) {
            this.logger.info("getting build id " + runner.getBuild().getBuildId());
            this.logger.info("Plugin is enabled, starting patch process");
            this.appConfig.setWorkingDir(runner.getWorkingDirectory().getPath());
            this.logger.info("working dir = " + this.appConfig.getWorkingDir());
            new ApplyPatch(runner, this.appConfig).run();
            this.conduitClient = new ConduitClient(this.appConfig.getPhabricatorUrl(), this.appConfig.getPhabricatorProtocol(), this.appConfig.getConduitToken(), logger);
            this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build started: " + this.serverUrl + "/viewLog.html?buildId=" + runner.getBuild().getBuildId());
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
        this.refreshConfig(build);
        if(this.appConfig.isEnabled()) {
            String buildInfo = this.serverUrl + "/viewLog.html?buildId=" + build.getBuildId();
            if(status.isFailed() && status.isFinished()) {
                this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build failed: " + buildInfo);
            } else if (!status.isFailed() && status.isFinished()) {
                this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build successful: " +buildInfo);
            } else {
                this.conduitClient.submitDifferentialComment(this.appConfig.getRevisionId(), "Build Error : " +buildInfo);
            }
        }
    }
}
