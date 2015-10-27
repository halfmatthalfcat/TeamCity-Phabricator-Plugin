package com.couchmate.teamcity.arcanist;

import com.couchmate.teamcity.TCPhabException;
import com.couchmate.teamcity.utils.CommandBuilder;
import com.couchmate.teamcity.utils.KeyValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mjo20 on 10/10/2015.
 */
public final class ArcanistClient {

    private final String conduitToken;
    private final String workingDir;
    private final String ARC_COMMAND = "arc";

    private ArcanistClient(){
        this.conduitToken = null;
        this.workingDir = null;
    }

    public ArcanistClient(
            final String conduitToken,
            final String workingDir
    ){
        this.conduitToken = conduitToken;
        this.workingDir = workingDir;
    }

    /**
     * @param diffId The differential id to apply
     * @return {@link com.couchmate.teamcity.utils.CommandBuilder.Command} The built Command
     */
    public CommandBuilder.Command patch(
            final String diffId
    ){
        try {
            return new CommandBuilder()
                    .setCommand(this.ARC_COMMAND)
                    .setAction("patch")
                    .setWorkingDir(this.workingDir)
                    .setFlag("--nobranch")
                    .setFlagWithValue(new KeyValue("--diff", formatDiffId(diffId)))
                    .setFlagWithValue(new KeyValue("--conduit-token", this.conduitToken))
                    .build();
        } catch (TCPhabException e) { return null; }
    }

    private static String formatDiffId(String diffId) throws TCPhabException {
        Pattern diffIdWithD = Pattern.compile("^D[0-9]+$");
        Pattern diffIdWoD = Pattern.compile("^[0-9]+$");
        Matcher m = diffIdWithD.matcher(diffId);
        Matcher m1 = diffIdWoD.matcher(diffId);
        if(m.matches()) return diffId;
        else if(m1.matches()) return String.format("D%s", diffId);
        else throw new TCPhabException(String.format("Invalid Differential DiffId %s", diffId));
    }

}
