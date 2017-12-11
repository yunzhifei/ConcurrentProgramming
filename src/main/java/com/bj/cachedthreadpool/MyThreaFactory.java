package com.bj.cachedthreadpool;

import java.util.concurrent.ThreadFactory;

public class MyThreaFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }
}
