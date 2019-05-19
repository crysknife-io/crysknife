package org.treblereel.gwt.crysknife.client;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.App;

public class AppBootstrap {

    private App instance;

    void initialize() {
        this.instance.init();
    }

     AppBootstrap(App application) {
        this.instance = application;
    }
}
