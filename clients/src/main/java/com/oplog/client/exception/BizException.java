package com.oplog.client.exception;

import java.net.SocketAddress;


public class BizException extends RemoteException {

    private static final long serialVersionUID = -3996155413840689423L;

    public BizException(Throwable cause, SocketAddress remoteAddress) {
        super(cause, remoteAddress);
    }

    public BizException(String message, SocketAddress remoteAddress) {
        super(message, remoteAddress);
    }

    public BizException(String message, Throwable cause, SocketAddress remoteAddress) {
        super(message, cause, remoteAddress);
    }
}
