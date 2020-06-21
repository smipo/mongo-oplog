package com.oplog.netty;

import com.oplog.netty.handler.ServerHandler;
import com.oplog.netty.protocol.ProtocolDecoder;
import com.oplog.netty.protocol.ProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyServer {

    private final SocketAddress localAddress;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private AcceptorIdleStateTrigger trigger = new AcceptorIdleStateTrigger();

    public NettyServer(int port){
        this.localAddress = new InetSocketAddress(port);
    }

    public NettyServer(SocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public NettyServer(String host,int port){
        this.localAddress = new InetSocketAddress(host,port);
    }

    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //心跳机制 参数:1.读空闲超时时间 2.写空闲超时时间 3.所有类型的空闲超时时间(读、写) 4.时间单位
                        //在Handler需要实现userEventTriggered方法，在出现超时事件时会被触发
                        socketChannel.pipeline().addLast( new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new ProtocolDecoder());
                        socketChannel.pipeline().addLast( new ProtocolEncoder());
                        socketChannel.pipeline().addLast( new ServerHandler());
                        socketChannel.pipeline().addLast(trigger);
                    }
                });
        ChannelFuture future = bootstrap.bind(localAddress).sync();
        future.channel().closeFuture().sync();
    }

    public void stop(){
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
