package com.airxiechao.y20.test;

import com.airxiechao.y20.common.core.pubsub.EventBus;

public class MqTest {

    public static void main(String[] args) throws Exception {
        EventBus.getInstance();

        Thread.currentThread().join();
    }
}
