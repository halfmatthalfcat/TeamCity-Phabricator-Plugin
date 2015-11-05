package com.couchmate.teamcity.phabricator;

import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by mjo20 on 10/27/2015.
 */
public final class PhabLogger {

    @NonNls
    private static final String AGENT_BLOCK = "agent";
    private static final String ACTIVITY_NAME = "Phabricator";

    @Nullable
    BuildProgressLogger buildProgressLogger;

    public void setBuildLogger(@Nullable BuildProgressLogger buildLogger){
        this.buildProgressLogger = buildLogger;
    }

    public void info(String message){
        Loggers.SERVER.info(String.format("Phabricator Plugin: %s", message));
    }

    public void warn(String message, Exception e){
        Loggers.SERVER.warn(message, e);
    }

    public void serverInfo(String message){
        Loggers.SERVER.info(message);
    }

}
