package com.couchmate.teamcity.phabricator;

import java.util.IllegalFormatException;

public class KeyValue<K, V> {

    private final K key;
    private final V value;

    private KeyValue(){
        this.key = null;
        this.value = null;
    }

    public KeyValue(
            final K key,
            final V value
    ){
        if(isNullOrEmpty(key)) throw new IllegalArgumentException("Must provide a valid key");
        else this.key = key;
        this.value = value;
    }

    private static Boolean isNullOrEmpty(Object o){
        if(o == null) return true;
        else if (String.valueOf(o).equals("")) return true;
        else return false;
    }

    public K getKey(){
        return this.key;
    }

    public V getValue(){
        return this.value;
    }

}
