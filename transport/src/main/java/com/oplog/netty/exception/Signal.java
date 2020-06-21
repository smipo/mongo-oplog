package com.oplog.netty.exception;

public class Signal extends Exception {

    public Signal(){
        super();
    }

    public Signal(String message) {
        super(message);
    }

    public Signal(String message, Throwable cause) {
        super(message, cause);
    }

    public Signal(Throwable cause) {
        super(cause);
    }
}
