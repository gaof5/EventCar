package com.gaof.event;

/**
 * 用来处理接收与发送的线程关系
 */
public enum ThreadMode {

    /**
     * 发送方和接受方在同一线程
     */
    PostThread,
    /**
     * 接收方在主线程
     */
    MainThread,
    /**
     * 接受方在子线程
     */
    BackgroundThread;

    ThreadMode() {
    }
}
