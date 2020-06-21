package com.oplog.client.future;

import com.oplog.netty.payload.ResponsePayload;
import com.oplog.netty.Status;

public class Response {

    private final ResponsePayload payload;     // 响应bytes/stream

    private Object result; // 响应结果对象, 也可能是异常对象

    public Response(long id) {
        payload = new ResponsePayload(id);
    }

    public Response(ResponsePayload payload) {
        this.payload = payload;
    }

    public ResponsePayload payload() {
        return payload;
    }

    public long id() {
        return payload.invokeId();
    }

    public byte status() {
        return payload.status();
    }

    public void status(byte status) {
        payload.status(status);
    }

    public void status(Status status) {
        payload.status(status.value());
    }


    public Object result() {
        return result;
    }

    public void result(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "JResponse{" +
                "status=" + Status.parse(status()) +
                ", id=" + id() +
                ", result=" + result +
                '}';
    }
}
