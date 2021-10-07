package io.crysknife.ui.databinding.client;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/6/21
 */
@ApplicationScoped
@Startup
public class DataBinderInitializer {

    @PostConstruct
    void init() {
        loadBindableProxies();
    }

    public static native void loadBindableProxies() /*-{
        @io.crysknife.ui.databinding.client.api.DataBinder_Factory::get()();
    }-*/;

}
