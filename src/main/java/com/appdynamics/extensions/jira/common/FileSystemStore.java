/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
package com.appdynamics.extensions.jira.common;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by balakrishnavadavalasa on 15/09/16.
 */
public enum FileSystemStore {
    INSTANCE;

    private DB FILE_DB;
    private ConcurrentMap<String, String> DB_MAP;

    FileSystemStore() {
        FILE_DB = DBMaker.fileDB("file.db").make();
        DB_MAP = FILE_DB.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
    }


    public String getFromStore(String key) {

        return DB_MAP.get(key);
    }

    public void putInStore(String key, String value) {
        DB_MAP.put(key, value);
        FILE_DB.commit();
    }

    public void removeFromStore(String key) {
        DB_MAP.remove(key);
        FILE_DB.commit();
    }

    public void closeStore() {
        FILE_DB.close();
    }
}
