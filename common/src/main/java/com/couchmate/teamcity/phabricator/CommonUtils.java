package com.couchmate.teamcity.phabricator;

/**
 * Created by mjo20 on 10/10/2015.
 */
public final class CommonUtils {

    public static Boolean isNullOrEmpty(String str){
        return str == null || str.equals("");
    }

}
