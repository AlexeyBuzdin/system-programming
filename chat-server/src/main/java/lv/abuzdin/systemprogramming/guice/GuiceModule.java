package lv.abuzdin.systemprogramming.guice;

import com.google.inject.AbstractModule;
import lv.abuzdin.systemprogramming.server.ServerRunningState;
import lv.abuzdin.systemprogramming.server.jobs.SocketConnectorJob;

import static com.google.inject.Scopes.SINGLETON;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServerRunningState.class).in(SINGLETON);
        bind(SocketConnectorJob.class).in(SINGLETON);
    }

}
