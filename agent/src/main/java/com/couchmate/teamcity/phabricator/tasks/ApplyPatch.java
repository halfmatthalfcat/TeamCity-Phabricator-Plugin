package com.couchmate.teamcity.phabricator.tasks;

import com.couchmate.teamcity.phabricator.AppConfig;
import com.couchmate.teamcity.phabricator.CommandBuilder;
import com.couchmate.teamcity.phabricator.PhabLogger;
import com.couchmate.teamcity.phabricator.arcanist.ArcanistClient;
import com.couchmate.teamcity.phabricator.conduit.ConduitClient;
import com.couchmate.teamcity.phabricator.git.GitClient;
import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;

/**
 * Created by mjo20 on 10/15/2015.
 */
public class ApplyPatch extends Task {

    private BuildProgressLogger logger;
    private AppConfig appConfig;
    private GitClient gitClient = null;
    private ArcanistClient arcanistClient = null;
    private BuildRunnerContext runner;
    private BuildProblemData buildProblem;

    private static final String GIT_PATCH_FAILURE = "GIT_PATCH_FAILURE";
    private static final String ARC_PATCH_FAILURE = "ARC_PATCH_FAILURE";
    private static final String GENERAL_PATCH_FAILURE = "GENERAL_PATCH_FAILURE";

    public ApplyPatch(BuildRunnerContext runner, AppConfig appConfig){
        this.appConfig = appConfig;
        this.logger = runner.getBuild().getBuildLogger();
        this.runner = runner;
    }

    @Override
    protected void setup() {
        this.logger.activityStarted("Phabricator Plugin", "Applying Differential Patch " + appConfig.getDiffId());
        this.gitClient = new GitClient(this.appConfig.getWorkingDir());
        this.arcanistClient = new ArcanistClient(
                this.appConfig.getConduitToken(), this.appConfig.getWorkingDir(), this.appConfig.getArcPath());
    }

    @Override
    protected void execute() {
        int cleanCode, patchCode, resetCode;
        try {
            CommandBuilder.Command reset = gitClient.reset();
            resetCode = reset.exec().join();
            this.logger.message(String.format("Reset exited with code: %d", resetCode));
            CommandBuilder.Command clean = gitClient.clean();
            cleanCode = clean.exec().join();
            this.logger.message(String.format("Clean exited with code: %d", cleanCode));
            if(resetCode > 0 || cleanCode > 0 ) {
               buildProblem = BuildProblemData.createBuildProblem(GIT_PATCH_FAILURE,
                                                                    GIT_PATCH_FAILURE,
                                                                    "Unable to git reset or git clean arc diff " + this.appConfig.getDiffId());
               this.logger.logBuildProblem(buildProblem);
            }
            CommandBuilder.Command patch = arcanistClient.patch(this.appConfig.getRevisionId());
            patchCode = patch.exec().join();
            this.logger.message(String.format("Patch exited with code: %d", patchCode));
            if(patchCode > 0){
                buildProblem = BuildProblemData.createBuildProblem(ARC_PATCH_FAILURE,
                                                                    ARC_PATCH_FAILURE, 
                                                                    "Unable to patch master with this arc diff " + this.appConfig.getDiffId());
                this.logger.logBuildProblem(buildProblem);
            }
            
          this.logger.activityFinished("Phabricator Plugin", "Finished Applying Differential Patch " + this.appConfig.getDiffId());
        } catch (NullPointerException e) {  
                buildProblem = BuildProblemData.createBuildProblem(GENERAL_PATCH_FAILURE,
                                                                    GENERAL_PATCH_FAILURE,
                                                                    "Patching caused a general exception e = " + e.getMessage());  
        }
    }

    @Override
    protected void teardown() {

    }
}
