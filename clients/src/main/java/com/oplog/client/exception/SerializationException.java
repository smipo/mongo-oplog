package com.oplog.client.exception;

import java.net.SocketAddress;


public class SerializationException extends RemoteException {

    private static final long serialVersionUID = -5079093080483380586L;

    public SerializationException() {}

    public SerializationException(SocketAddress remoteAddress) {
        super(remoteAddress);
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, SocketAddress remoteAddress) {
        super(message, remoteAddress);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }

    public SerializationException(Throwable cause, SocketAddress remoteAddress) {
        super(cause, remoteAddress);
    }
}
