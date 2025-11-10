package net.normalv.event.events;

import net.normalv.event.Event;

public class TestEvent extends Event {
    private String msg;

    public TestEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
