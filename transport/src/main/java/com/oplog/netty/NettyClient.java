package com.oplog.netty;

import com.oplog.netty.handler.ClientHandler;
import com.oplog.netty.protocol.ProtocolDecoder;
import com.oplog.netty.protocol.ProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyClient {

    private String host;

    private int port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private ConnectorIdleStateTrigger trigger = new ConnectorIdleStateTrigger();

    public NettyClient(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //心跳机制 参数:1.读空闲超时时间 2.写空闲超时时间 3.所有类型的空闲超时时间(读、写) 4.时间单位
                        //在Handler需要实现userEventTriggered方法，在出现超时事件时会被触发
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new ProtocolDecoder());
                        socketChannel.pipeline().addLast(new ProtocolEncoder());
                        socketChannel.pipeline().addLast(trigger);
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });

        // 客户端开启
        ChannelFuture cf = bootstrap.connect(host, port).sync();

    }

    public void stop() {
        bossGroup.shutdownGracefully();
    }
}
