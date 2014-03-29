package lv.abuzdin.systemprogramming.lesson3.guice;

import com.google.inject.AbstractModule;
import lv.abuzdin.systemprogramming.lesson3.server.ServerRunningState;

import static com.google.inject.Scopes.SINGLETON;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServerRunningState.class).in(SINGLETON);
    }

}