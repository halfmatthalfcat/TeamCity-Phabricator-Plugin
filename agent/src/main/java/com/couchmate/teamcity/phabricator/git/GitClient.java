package com.couchmate.teamcity.phabricator.git;

import com.couchmate.teamcity.phabricator.CommandBuilder;
import com.couchmate.teamcity.phabricator.TCPhabException;

/**
 * Created by mjo20 on 10/12/2015.
 */
public final class GitClient {

    private final String GIT_COMMAND = "git";
    private final String workingDir;

    private GitClient(){
        this.workingDir = null;
    }

    public GitClient(final String workingDir){
        this.workingDir = workingDir;
    }

    public CommandBuilder.Command reset(){
        return new CommandBuilder()
                .setWorkingDir(this.workingDir)
                .setCommand(this.GIT_COMMAND)
                .setAction("reset")
                .setFlag("--hard")
                .build();
    }

    public CommandBuilder.Command clean(){
        return new CommandBuilder()
                .setWorkingDir(this.workingDir)
                .setCommand(this.GIT_COMMAND)
                .setAction("clean")
                .setArg("-fd")
                .setArg("-f")
                .build();
    }

}
