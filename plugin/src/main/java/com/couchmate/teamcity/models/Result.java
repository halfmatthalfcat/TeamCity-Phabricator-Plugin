package com.couchmate.teamcity.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjo20 on 10/13/2015.
 */
public class Result {

    public String result;
    @SerializedName("error_code")
    public String errorCode;
    @SerializedName("error_info")
    public String errorInfo;

}
