package com.oplog.netty.payload;


public class ResponsePayload extends PayloadHolder {

    // 用于映射 <id, request, response> 三元组
    private final long invokeId; // request.invokeId
    private byte status;

    public ResponsePayload(long invokeId) {
        this.invokeId = invokeId;
    }

    public long invokeId() {
        return invokeId;
    }

    public byte status() {
        return status;
    }

    public void status(byte status) {
        this.status = status;
    }
}
