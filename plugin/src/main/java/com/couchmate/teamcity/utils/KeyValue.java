package com.couchmate.teamcity.utils;

/**
 * Created by mjo20 on 10/10/2015.
 */
public class KeyValue {

    private final String key;
    private final String value;

    private KeyValue(){
        this.key = null;
        this.value = null;
    }

    public KeyValue(
            final String key,
            final String value
    ){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

}
