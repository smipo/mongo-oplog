package com.oplog.netty.protocol;

import com.oplog.netty.exception.Signal;
import com.oplog.netty.payload.PayloadHolder;
import com.oplog.netty.payload.RequestPayload;
import com.oplog.netty.payload.ResponsePayload;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

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
public class ProtocolEncoder extends MessageToByteEncoder<PayloadHolder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PayloadHolder msg, ByteBuf out) throws Exception {
        if (msg instanceof RequestPayload) {
            doEncodeRequest((RequestPayload) msg, out);
        } else if (msg instanceof ResponsePayload) {
            doEncodeResponse((ResponsePayload) msg, out);
        } else {
            throw new Signal(msg == null?"null class":msg.getClass().getName());
        }
    }


    private void doEncodeRequest(RequestPayload request, ByteBuf out) {
        byte sign = ProtocolHeader.REQUEST;
        long invokeId = request.invokeId();
        byte[] bytes = request.bytes();
        int length = bytes.length;

        out.writeShort(ProtocolHeader.MAGIC)
                .writeByte(sign)
                .writeByte(0x00)
                .writeLong(invokeId)
                .writeInt(length)
                .writeBytes(bytes);
    }

    private void doEncodeResponse(ResponsePayload response, ByteBuf out) {
        byte sign = ProtocolHeader.RESPONSE;
        byte status = response.status();
        long invokeId = response.invokeId();
        byte[] bytes = response.bytes();
        int length = bytes.length;

        out.writeShort(ProtocolHeader.MAGIC)
                .writeByte(sign)
                .writeByte(status)
                .writeLong(invokeId)
                .writeInt(length)
                .writeBytes(bytes);
    }
}
