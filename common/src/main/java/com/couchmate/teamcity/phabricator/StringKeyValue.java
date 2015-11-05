package com.couchmate.teamcity.phabricator;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap;

/**
 * Created by matoliver15 on 10/30/15.
 */
public final class StringKeyValue extends KeyValue<String, String> {

    public StringKeyValue(
            final String key,
            final String value
    ){
        super(key, value);
    }

}
