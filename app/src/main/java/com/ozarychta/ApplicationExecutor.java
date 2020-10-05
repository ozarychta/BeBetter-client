package com.ozarychta;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApplicationExecutor {

    private static ApplicationExecutor INSTANCE = null;
    private Executor executor;

    private ApplicationExecutor(Executor executor) {
        this.executor = executor;
    }

    public static synchronized ApplicationExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationExecutor(Executors.newFixedThreadPool(3));
        }
        return INSTANCE;
    }

    public Executor getExecutor() {
        return executor;
    }
}
