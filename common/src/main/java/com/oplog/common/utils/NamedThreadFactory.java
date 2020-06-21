package com.oplog.common.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class NamedThreadFactory implements ThreadFactory {

    private static Logger logger = LogManager.getLogger(NamedThreadFactory.class);

    private final AtomicInteger id = new AtomicInteger();
    private final String name;
    private final boolean daemon;
    private final int priority;
    private final ThreadGroup group;

    public NamedThreadFactory(String name) {
        this(name, false, Thread.NORM_PRIORITY);
    }

    public NamedThreadFactory(String name, boolean daemon) {
        this(name, daemon, Thread.NORM_PRIORITY);
    }

    public NamedThreadFactory(String name, int priority) {
        this(name, false, priority);
    }

    public NamedThreadFactory(String name, boolean daemon, int priority) {
        this.name = name + " #";
        this.daemon = daemon;
        this.priority = priority;
        SecurityManager s = System.getSecurityManager();
        group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Requires.requireNotNull(r, "runnable");

        String name2 = name + id.getAndIncrement();

        Runnable r2 = r;

        Thread t = new Thread(group, r2, name2);

        try {
            if (t.isDaemon() != daemon) {
                t.setDaemon(daemon);
            }

            if (t.getPriority() != priority) {
                t.setPriority(priority);
            }
        } catch (Exception ignored) { /* doesn't matter even if failed to set. */ }

        logger.info("Creates new {}.", t);

        return t;
    }

    public ThreadGroup getThreadGroup() {
        return group;
    }


}
