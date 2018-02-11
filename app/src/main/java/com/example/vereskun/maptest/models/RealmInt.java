package com.example.vereskun.maptest.models;

import io.realm.RealmObject;

/**
 * Created by vereskun on 11.02.2018.
 */

public class RealmInt extends RealmObject {

    private Integer integer;

    public RealmInt(Integer integer) {
        this.integer = integer;
    }

    public RealmInt() {
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
