package com.couchmate.teamcity.phabricator.tasks;

import com.couchmate.teamcity.phabricator.AppConfig;
import com.couchmate.teamcity.phabricator.arcanist.ArcanistClient;
import com.couchmate.teamcity.phabricator.conduit.ConduitClient;
import com.couchmate.teamcity.phabricator.git.GitClient;
import jetbrains.buildServer.agent.BuildProgressLogger;

/**
 * Created by mjo20 on 10/15/2015.
 */
public class ApplyPatch extends Task {

    private BuildProgressLogger logger;
    private AppConfig appConfig;
    private GitClient gitClient = null;
    private ArcanistClient arcanistClient = null;
    private ConduitClient conduitClient = null;

    public ApplyPatch(AppConfig appConfig, BuildProgressLogger logger){
        this.appConfig = appConfig;
        this.logger = logger;
    }

    @Override
    protected void setup() {
        logger.message(String.format("Phabricator Plugin: Applying Differential Patch %s", appConfig.getDiffId()));
        this.gitClient = new GitClient(this.appConfig.getWorkingDir());
        this.arcanistClient = new ArcanistClient(this.appConfig.getConduitToken(), this.appConfig.getWorkingDir());
        this.conduitClient = new ConduitClient(this.appConfig.getPhabricatorUrl(), this.appConfig.getConduitToken());
    }

    @Override
    protected void execute() {
        try {
            gitClient.reset().exec();
            gitClient.clean().exec();
            arcanistClient.patch(this.appConfig.getDiffId()).exec();
        } catch (NullPointerException e) { logger.warning(String.format("Error while applying patch %s\n%s",
                this.appConfig.getDiffId(), e.getMessage())); }
    }

    @Override
    protected void teardown() {

    }
}
