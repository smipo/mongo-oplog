package com.oplog.client.exception;


import com.oplog.netty.Status;

import java.net.SocketAddress;


public class TimeoutException extends RemoteException {

    private static final long serialVersionUID = 8768621104391094458L;

    private final Status status;

    public TimeoutException(SocketAddress remoteAddress, Status status) {
        super(remoteAddress);
        this.status = status;
    }

    public TimeoutException(Throwable cause, SocketAddress remoteAddress, Status status) {
        super(cause, remoteAddress);
        this.status = status;
    }

    public TimeoutException(String message, SocketAddress remoteAddress, Status status) {
        super(message, remoteAddress);
        this.status = status;
    }

    public TimeoutException(String message, Throwable cause, SocketAddress remoteAddress, Status status) {
        super(message, cause, remoteAddress);
        this.status = status;
    }

    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        return "TimeoutException{" +
                "status=" + status +
                '}';
    }
}
