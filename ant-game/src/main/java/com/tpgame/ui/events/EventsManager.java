package com.tpgame.ui.events;

import com.google.common.eventbus.EventBus;

import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
@Singleton
public class EventsManager {
    private final EventBus eventBus = new EventBus();
    private final Executor eventsProcessor = Executors.newCachedThreadPool();

    public void post(Object object) {
        eventBus.post(object);
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    public void doAsync(Runnable runnable) {
        eventsProcessor.execute(runnable);
    }
}
