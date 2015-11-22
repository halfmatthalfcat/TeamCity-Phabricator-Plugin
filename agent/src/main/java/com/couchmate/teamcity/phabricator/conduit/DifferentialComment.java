package com.couchmate.teamcity.phabricator.conduit;

import com.google.gson.annotations.SerializedName;

public final class DifferentialComment extends MessageBase {

    @SerializedName("revision_id")
    private final String diffId;
    @SerializedName("message")
    private final String comment;

    private DifferentialComment(){
        super(null);
        this.diffId = null;
        this.comment = null;
    }

    public DifferentialComment(
            final String apiKey,
            final String diffId,
            final String comment
    ){
        super(apiKey);
        this.diffId = diffId;
        this.comment = comment;
    }

}
