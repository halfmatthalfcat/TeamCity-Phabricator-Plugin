package com.couchmate.teamcity.phabricator.arcanist;

import com.couchmate.teamcity.phabricator.CommandBuilder;
import com.couchmate.teamcity.phabricator.KeyValue;
import com.couchmate.teamcity.phabricator.StringKeyValue;
import com.couchmate.teamcity.phabricator.TCPhabException;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ArcanistClient {

    private final String conduitToken;
    private final String workingDir;
    private final String arcPath;

    private ArcanistClient(){
        this.conduitToken = null;
        this.workingDir = null;
        this.arcPath = null;
    }

    public ArcanistClient(
            final String conduitToken,
            final String workingDir,
            final String arcPath
    ){
        this.conduitToken = conduitToken;
        this.workingDir = workingDir;
        this.arcPath = arcPath;
    }

    /**
     * @param diffId The differential id to apply
     * @return {@link CommandBuilder.Command} The built Command
     */
    public CommandBuilder.Command patch(
            final String diffId
    ){
        try {
            return new CommandBuilder()
                    .setCommand(arcPath)
                    .setAction("patch")
                    .setWorkingDir(this.workingDir)
                    .setFlag("--nobranch")
                    .setFlag("--nocommit")
                    .setArg("--diff")
                    .setArg(formatDiffId(diffId))
                    .setFlagWithValueEquals(new StringKeyValue("--conduit-token", this.conduitToken))
                    .build();
        } catch (TCPhabException e) { e.printStackTrace(); return null; }
    }

    public CommandBuilder.Command which(
            final String prog
    ){
        return new CommandBuilder()
                .setCommand("which")
                .setAction(prog)
                .build();
    }

    private static String formatDiffId(String diffId) throws TCPhabException {
        Pattern diffIdWithD = Pattern.compile("^D([0-9]+)$");
        Pattern diffIdWoD = Pattern.compile("^[0-9]+$");
        Matcher m = diffIdWithD.matcher(diffId);
        Matcher m1 = diffIdWoD.matcher(diffId);
        if(m.matches()) return m.group();
        else if(m1.matches()) return diffId;
        else throw new TCPhabException(String.format("Invalid Differential DiffId %s", diffId));
    }

}
