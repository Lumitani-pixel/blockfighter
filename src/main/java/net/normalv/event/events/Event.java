package net.normalv.event.events;

public class Event{
    private boolean cancelled = false;

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}