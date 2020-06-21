package com.oplog.core.parse;

import com.oplog.common.parse.OplogParse;

public interface OplogProcess {

    void process(OplogParse oplogParse) throws Exception;
}
