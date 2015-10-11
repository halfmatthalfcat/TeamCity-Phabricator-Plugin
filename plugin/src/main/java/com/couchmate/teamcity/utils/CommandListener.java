package com.couchmate.teamcity.utils;

/**
 * Created by mjo20 on 10/11/2015.
 */
public interface CommandListener {

    void commandCompleted(int code, String output);

}
