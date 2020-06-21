package com.oplog.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LogManager.getLogger(ClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel ch = ctx.channel();
        logger.error("Unexpected exception was caught: {}, channel: {}.", cause, ch);
        ch.close();
    }

}
