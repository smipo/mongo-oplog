package com.oplog.netty.channel;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;

public class NettyChannel {

    private static final AttributeKey<NettyChannel> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");

    /**
     * Returns the {@link NettyChannel} for given {@link Channel}, this method never return null.
     */
    public static NettyChannel attachChannel(Channel channel) {
        Attribute<NettyChannel> attr = channel.attr(NETTY_CHANNEL_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel(channel);
            nChannel = attr.setIfAbsent(newNChannel);
            if (nChannel == null) {
                nChannel = newNChannel;
            }
        }
        return nChannel;
    }

    private final Channel channel;

    private NettyChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel channel() {
        return channel;
    }

    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }
}
