package com.tpgame.core.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Artemik on 16.11.2015
 * @version $Id: $
 */
public class AppBeans {
    private final static Injector injector = Guice.createInjector(new CustomModule());

    public static <T> T get(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    private static class CustomModule extends AbstractModule {
        @Override
        protected void configure() {}
    }
}
