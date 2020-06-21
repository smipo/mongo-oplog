package com.oplog.client.future;

import java.util.concurrent.CompletionStage;


public interface InvokeFuture<V> extends CompletionStage<V> {

    V getResult() throws Throwable;

    void markSent();
}
