package com.couchmate.teamcity.phabricator;

public final class CommonUtils {

    public static Boolean isNullOrEmpty(String str){
        return str == null || str.equals("");
    }

}
