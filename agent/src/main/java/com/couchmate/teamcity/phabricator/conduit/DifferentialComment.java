package com.couchmate.teamcity.phabricator.conduit;

import com.google.gson.annotations.SerializedName;

public final class DifferentialComment extends MessageBase {

    @SerializedName("revision_id")
    private final String revisionId;
    @SerializedName("message")
    private final String comment;

    private DifferentialComment(){
        super(null);
        this.revisionId = null;
        this.comment = null;
    }

    public DifferentialComment(
            final String apiKey,
            final String revisionId,
            final String comment
    ){
        super(apiKey);
        this.revisionId = revisionId;
        this.comment = comment;
    }

}
