package net.normalv.event.events.impl;

import net.normalv.event.events.Event;

public class ChatEvent extends Event {
    private String content;

    public ChatEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
