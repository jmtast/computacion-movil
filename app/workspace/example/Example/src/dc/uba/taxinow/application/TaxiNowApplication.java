package dc.uba.taxinow.application;

import android.app.Application;

public class TaxiNowApplication extends Application {
    @Override
    public void onCreate() {
        registerActivityLifecycleCallbacks(new LifecycleHandler());
    }
}
