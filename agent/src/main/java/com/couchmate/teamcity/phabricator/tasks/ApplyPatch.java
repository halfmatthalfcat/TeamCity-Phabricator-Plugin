package com.couchmate.teamcity.phabricator.tasks;

import com.couchmate.teamcity.phabricator.AppConfig;
import com.couchmate.teamcity.phabricator.CommandBuilder;
import com.couchmate.teamcity.phabricator.PhabLogger;
import com.couchmate.teamcity.phabricator.arcanist.ArcanistClient;
import com.couchmate.teamcity.phabricator.conduit.ConduitClient;
import com.couchmate.teamcity.phabricator.git.GitClient;
import jetbrains.buildServer.agent.BuildRunnerContext;

/**
 * Created by mjo20 on 10/15/2015.
 */
public class ApplyPatch extends Task {

    private PhabLogger logger;
    private AppConfig appConfig;
    private GitClient gitClient = null;
    private ArcanistClient arcanistClient = null;
    private BuildRunnerContext runner;

    public ApplyPatch(BuildRunnerContext runner, AppConfig appConfig, PhabLogger logger){
        this.appConfig = appConfig;
        this.logger = logger;
        this.runner = runner;
    }

    @Override
    protected void setup() {
        logger.info(String.format("Phabricator Plugin: Applying Differential Patch %s", appConfig.getDiffId()));
        this.gitClient = new GitClient(this.appConfig.getWorkingDir());
        this.arcanistClient = new ArcanistClient(
                this.appConfig.getConduitToken(), this.appConfig.getWorkingDir(), this.appConfig.getArcPath());
    }

    @Override
    protected void execute() {
        try {
            CommandBuilder.Command reset = gitClient.reset();
            int resetCode = reset.exec().join();
            logger.info(String.format("Reset exited with code: %d", resetCode));

            CommandBuilder.Command clean = gitClient.clean();
            int cleanCode = clean.exec().join();
            logger.info(String.format("Clean exited with code: %d", cleanCode));

            CommandBuilder.Command patch = arcanistClient.patch(this.appConfig.getRevisionId());
            int patchCode = patch.exec().join();
            logger.info(String.format("Patch exited with code: %d", patchCode));

            if(patchCode > 0){
                this.runner.getBuild().stopBuild("Patch failed to apply. Check the agent output log for patch failure detals.");
            }

        } catch (NullPointerException e) { logger.warn("AppPatchError", e); }
    }

    @Override
    protected void teardown() {

    }
}
