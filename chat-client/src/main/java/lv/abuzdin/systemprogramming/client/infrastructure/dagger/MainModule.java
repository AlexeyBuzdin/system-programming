package lv.abuzdin.systemprogramming.client.infrastructure.dagger;

import android.content.Context;
import android.view.LayoutInflater;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import lv.abuzdin.systemprogramming.client.BaseApplication;
import lv.abuzdin.systemprogramming.client.presentation.MainActivity;

import javax.inject.Singleton;

@Module(
        injects = {
                BaseApplication.class,
                MainActivity.class
        }
)
public class MainModule implements DaggerModule {

    private final Context appContext;

    public MainModule(Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }

    @Provides @Singleton
    Bus provideBus() {
        return new Bus();
    }

    @Provides
    Context provideContext() {
        return appContext;
    }

    @Provides
    LayoutInflater provideLayoutInflater() {
        return LayoutInflater.from(appContext);
    }
}
