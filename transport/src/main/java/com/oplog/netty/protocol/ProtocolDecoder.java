package com.oplog.netty.protocol;

import com.oplog.netty.payload.RequestPayload;
import com.oplog.netty.payload.ResponsePayload;
import com.oplog.netty.exception.Signal;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;


import java.util.List;

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
public class ProtocolDecoder extends ReplayingDecoder<ProtocolDecoder.State> {


    public ProtocolDecoder() {
        super(State.MAGIC);
    }

    // 协议头
    private final ProtocolHeader header = new ProtocolHeader();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case MAGIC:
                checkMagic(in.readShort());         // MAGIC
                checkpoint(State.SIGN);
            case SIGN:
                header.messageCode(in.readByte());         // 消息标志位
                checkpoint(State.STATUS);
            case STATUS:
                header.status(in.readByte());       // 状态位
                checkpoint(State.ID);
            case ID:
                header.id(in.readLong());           // 消息id
                checkpoint(State.BODY_SIZE);
            case BODY_SIZE:
                header.bodySize(in.readInt());      // 消息体长度
                checkpoint(State.BODY);
            case BODY:
                switch (header.messageCode()) {
                    case ProtocolHeader.HEARTBEAT:
                        break;
                    case ProtocolHeader.REQUEST: {
                        int length = header.bodySize();
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);

                        RequestPayload request = new RequestPayload(header.id());
                        request.timestamp(System.currentTimeMillis());
                        request.bytes(bytes);

                        out.add(request);

                        break;
                    }
                    case ProtocolHeader.RESPONSE: {
                        int length = header.bodySize();
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);

                        ResponsePayload response = new ResponsePayload(header.id());
                        response.status(header.status());
                        response.bytes( bytes);

                        out.add(response);

                        break;
                    }
                    default:
                        throw new Signal("ILLEGAL_SIGN");
                }
                checkpoint(State.MAGIC);
        }
    }

    private static void checkMagic(short magic) throws Signal {
        if (magic != ProtocolHeader.MAGIC) {
            throw new Signal("ILLEGAL_MAGIC");
        }
    }

    enum State {
        MAGIC,
        SIGN,
        STATUS,
        ID,
        BODY_SIZE,
        BODY
    }
}
