package com.oplog.client.future;

import com.oplog.client.exception.BizException;
import com.oplog.client.exception.RemoteException;
import com.oplog.client.exception.SerializationException;
import com.oplog.netty.channel.NettyChannel;
import com.oplog.netty.Status;
import com.oplog.common.utils.NamedThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.concurrent.*;


public class DefaultInvokeFuture<V> extends CompletableFuture<V> implements InvokeFuture<V> {

    private static Logger logger = LogManager.getLogger(DefaultInvokeFuture.class);

    private static final long DEFAULT_TIMEOUT_NANOSECONDS = TimeUnit.MILLISECONDS.toNanos(3000);

    private static final ConcurrentMap<Long, DefaultInvokeFuture<?>> roundFutures =
            new ConcurrentHashMap<>(1024);


    private static final HashedWheelTimer timeoutScanner =
            new HashedWheelTimer(
                    new NamedThreadFactory("futures.timeout.scanner", true),
                    50, TimeUnit.MILLISECONDS,
                    4096
            );

    private final long invokeId; // request.invokeId
    private final NettyChannel channel;
    private final long timeout;
    private final long startTime = System.nanoTime();

    private volatile boolean sent = false;

    public static <T> DefaultInvokeFuture<T> with(
            long invokeId, NettyChannel channel, long timeoutMillis) {

        return new DefaultInvokeFuture<>(invokeId, channel, timeoutMillis);
    }

    private DefaultInvokeFuture(
            long invokeId, NettyChannel channel, long timeoutMillis) {

        this.invokeId = invokeId;
        this.channel = channel;
        this.timeout = timeoutMillis > 0 ? TimeUnit.MILLISECONDS.toNanos(timeoutMillis) : DEFAULT_TIMEOUT_NANOSECONDS;

        roundFutures.put(invokeId, this);
        TimeoutTask timeoutTask = new TimeoutTask(invokeId);

        timeoutScanner.newTimeout(timeoutTask, timeout, TimeUnit.NANOSECONDS);
    }

    public NettyChannel channel() {
        return channel;
    }


    @Override
    public V getResult() throws Throwable {
        try {
            return get(timeout, TimeUnit.NANOSECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            throw new com.oplog.client.exception.TimeoutException(e, channel.remoteAddress(),
                    sent ? Status.SERVER_TIMEOUT : Status.CLIENT_TIMEOUT);
        }
    }

    @Override
    public void markSent() {
        sent = true;
    }

    private void doReceived(Response response) {
        byte status = response.status();

        if (status == Status.OK.value()) {
            complete((V) response.result());
        } else {
            setException(status, response);
        }

    }
    private void setException(byte status, Response response) {
        Throwable cause;
        if (status == Status.SERVER_TIMEOUT.value()) {
            cause = new com.oplog.client.exception.TimeoutException(channel.remoteAddress(), Status.SERVER_TIMEOUT);
        } else if (status == Status.CLIENT_TIMEOUT.value()) {
            cause = new com.oplog.client.exception.TimeoutException(channel.remoteAddress(), Status.CLIENT_TIMEOUT);
        } else if (status == Status.DESERIALIZATION_FAIL.value()) {
            cause = (SerializationException) response.result();
        } else if (status == Status.SERVICE_EXPECTED_ERROR.value()) {
            cause = (Throwable) response.result();
        } else if (status == Status.SERVICE_UNEXPECTED_ERROR.value()) {
            String message = String.valueOf(response.result());
            cause = new BizException(message, channel.remoteAddress());
        } else {
            Object result = response.result();
            if (result instanceof RemoteException) {
                cause = (RemoteException) result;
            } else {
                cause = new RemoteException(response.toString(), channel.remoteAddress());
            }
        }
        completeExceptionally(cause);
    }

    public static void received(NettyChannel channel, Response response) {
        long invokeId = response.id();

        DefaultInvokeFuture<?> future = roundFutures.remove(invokeId);

        if (future == null) {
            logger.info("A timeout response [{}] finally returned on {}.", response, channel);
            return;
        }

        future.doReceived(response);
    }

    private static String subInvokeId(String channelId, long invokeId) {
        return channelId + invokeId;
    }

    static final class TimeoutTask implements TimerTask {

        private final long invokeId;

        public TimeoutTask(long invokeId) {
            this.invokeId = invokeId;
        }

        public TimeoutTask(String channelId, long invokeId) {
            this.invokeId = invokeId;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            DefaultInvokeFuture<?>  future = roundFutures.remove(invokeId);;

            if (future != null) {
                processTimeout(future);
            }
        }

        private void processTimeout(DefaultInvokeFuture<?> future) {
            if (System.nanoTime() - future.startTime > future.timeout) {
                Response response = new Response(future.invokeId);
                response.status(future.sent ? Status.SERVER_TIMEOUT : Status.CLIENT_TIMEOUT);

                future.doReceived(response);
            }
        }
    }
}
