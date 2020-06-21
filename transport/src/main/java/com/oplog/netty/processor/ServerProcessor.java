package com.oplog.netty.processor;

import com.oplog.netty.Status;
import com.oplog.netty.channel.NettyChannel;
import com.oplog.netty.payload.RequestPayload;

public interface ServerProcessor {

    void handleRequest(NettyChannel channel, RequestPayload request) throws Exception;

    void handleException(NettyChannel channel, RequestPayload request, Status status, Throwable cause);

    void shutdown();
}
