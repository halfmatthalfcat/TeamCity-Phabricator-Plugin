package com.couchmate.teamcity.phabricator;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.tests.TestInfo;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends BuildServerAdapter {

    private Map<String, List<STestRun>> tests = new HashMap<>();

    private PhabLogger logger;

    public Server(
            @NotNull final EventDispatcher<BuildServerListener> buildServerListener,
            @NotNull final PhabLogger logger
    ){
        buildServerListener.addListener(this);
        this.logger = logger;
        Loggers.SERVER.error("Phab Server Initialized");
    }

    @Override
    public void buildStarted(@NotNull SRunningBuild runningBuild){
        super.buildStarted(runningBuild);
        new Thread(new BuildTracker(runningBuild)).start();
    }
}
