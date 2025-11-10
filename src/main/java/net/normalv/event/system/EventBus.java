package net.normalv.event.system;

import net.normalv.event.events.Event;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {
    private final Map<Class<?>, List<ListenerMethod>> listeners = new HashMap<>();

    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1) {
                    Class<?> eventType = params[0];
                    listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                            .add(new ListenerMethod(listener, method));
                }
            }
        }
    }

    public void unregister(Object listener) {
        for (List<ListenerMethod> list : listeners.values()) {
            list.removeIf(lm -> lm.owner.equals(listener));
        }
    }

    public void post(Event event) {
        List<ListenerMethod> list = listeners.get(event.getClass());
        if (list == null) return;

        for (ListenerMethod lm : list) {
            if(event.isCancelled()) return;
            try {
                lm.method.setAccessible(true);
                lm.method.invoke(lm.owner, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private record ListenerMethod(Object owner, Method method) {}
}
