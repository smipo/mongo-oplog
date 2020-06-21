package com.oplog.client.future;

import com.oplog.common.utils.Requires;

public class InvokeFutureContext {

    private static final ThreadLocal<InvokeFuture<?>> futureThreadLocal = new ThreadLocal<>();

    public static InvokeFuture<?> future() {
        InvokeFuture<?> future = Requires.requireNotNull(futureThreadLocal.get(), "future");
        futureThreadLocal.remove();
        return future;
    }

    public static void set(InvokeFuture<?> future) {
        futureThreadLocal.set(future);
    }


}
