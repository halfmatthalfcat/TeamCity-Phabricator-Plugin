package com.couchmate.teamcity.phabricator.conduit;

import com.google.gson.annotations.SerializedName;

public class MessageBase {

    @SerializedName("api.key")
    private final String apiKey;

    private MessageBase(){
        this.apiKey = null;
    }

    public MessageBase(final String apiKey){
        this.apiKey = apiKey;
    }

}
