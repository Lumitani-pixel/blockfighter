package net.normalv.event.events.impl;

import net.normalv.event.events.Event;

public class TestEvent extends Event {
    private String msg;

    public TestEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
