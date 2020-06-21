package com.oplog.netty.payload;

import java.util.concurrent.atomic.AtomicLong;

public class RequestPayload extends PayloadHolder {

    private static final AtomicLong sequence = new AtomicLong();

    // 用于映射 <id, request, response> 三元组
    private final long invokeId;

    private transient long timestamp;

    public RequestPayload() {
        this(sequence.incrementAndGet());
    }

    public RequestPayload(long invokeId) {
        this.invokeId = invokeId;
    }

    public long invokeId() {
        return invokeId;
    }

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
