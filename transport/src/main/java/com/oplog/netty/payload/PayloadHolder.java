package com.oplog.netty.payload;


public abstract class PayloadHolder {

    private byte[] bytes;

    public byte[] bytes() {
        return bytes;
    }

    public void bytes(byte[] bytes) {
        this.bytes = bytes;
    }
    public int size() {
        return bytes == null ? 0 : bytes.length;
    }

}
