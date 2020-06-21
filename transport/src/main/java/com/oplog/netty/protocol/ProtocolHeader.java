package com.oplog.netty.protocol;

/**
 * 传输层协议头
 *
 * **************************************************************************************************
 *                                          Protocol
 *  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *       2   │   1   │    1   │     8     │      4      │
 *  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤
 *           │       │        │           │             │
 *  │  MAGIC   Sign    Status   Invoke Id    Body Size                    Body Content              │
 *           │       │        │           │             │
 *  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
 *
 * 消息头16个字节定长
 * = 2 // magic = (short) 0xbabe
 * + 1 // 消息标志位, 表示消息类型request/response/heartbeat等
 * + 1 // 状态位, 设置请求响应状态
 * + 8 // 消息 id, long 类型
 * + 4 // 消息体 body 长度, int 类型
 *
 */
public class ProtocolHeader {

    /** 协议头长度 */
    public static final int HEADER_SIZE = 16;
    /** Magic */
    public static final short MAGIC = (short) 0xbabe;

    public static final byte REQUEST                    = 0x01;     // Request
    public static final byte RESPONSE                   = 0x02;     // Response
    public static final byte HEARTBEAT                  = 0x03;     // Heartbeat
  //  public static final byte GET                        = 0x04;     // GET

    private byte messageCode;       // sign

    private byte status;            // 响应状态码
    private long id;                // request.invokeId, 用于映射 <id, request, response> 三元组
    private int bodySize;           // 消息体长度


    public void messageCode(byte messageCode) {
        this.messageCode = messageCode;
    }

    public byte messageCode() {
        return messageCode;
    }


    public byte status() {
        return status;
    }

    public void status(byte status) {
        this.status = status;
    }

    public long id() {
        return id;
    }

    public void id(long id) {
        this.id = id;
    }

    public int bodySize() {
        return bodySize;
    }

    public void bodySize(int bodyLength) {
        this.bodySize = bodyLength;
    }

    @Override
    public String toString() {
        return "ProtocolHeader{" +
                "messageCode=" + messageCode +
                ", status=" + status +
                ", id=" + id +
                ", bodySize=" + bodySize +
                '}';
    }
}
