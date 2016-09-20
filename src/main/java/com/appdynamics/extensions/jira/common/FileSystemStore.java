/**
 * Copyright 2016 AppDynamics, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
