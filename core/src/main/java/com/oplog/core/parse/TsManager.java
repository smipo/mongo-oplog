package com.oplog.core.parse;

public interface TsManager {

     String readLastTs() throws Exception;

     void writeLastTs(String ts) throws Exception;
}
