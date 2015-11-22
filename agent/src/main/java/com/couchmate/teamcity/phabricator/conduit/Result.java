package com.couchmate.teamcity.phabricator.conduit;

import com.google.gson.annotations.SerializedName;

public final class Result {

    private String result;
    @SerializedName("error_code")
    private String errorCode;
    @SerializedName("error_info")
    private String errorInfo;

    private Result(){}
    public Result(
            final String result,
            final String errorCode,
            final String errorInfo
    ){
        this.result = result;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    public String getResult(){ return this.result; }
    public String getErrorCode(){ return this.errorCode; }
    public String getErrorInfo(){ return this.errorInfo; }

}
