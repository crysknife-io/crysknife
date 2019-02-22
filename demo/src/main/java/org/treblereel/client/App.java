package org.treblereel.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
//@Application
//@ComponentScan("org.treblereel.client.inject")
public class App {

    @Inject
    ImageCompressor compressor;

    //@Inject
    public App(){
        RootPanel.get().add(new Button("APP"));
    }

   @PostConstruct
    public void init() {

        GWT.log("onModuleLoad");
        RootPanel.get().add(new Button("TEST"));



        test1();
        test1();
        test1();
        test1();

    }

    public void test1(){
/*        ImageCompressor compressor = ApplicationComponent.INSTANCE.imageCompressor();
        ApplicationComponent.INSTANCE.inject(compressor);

        compressor.compress("asd");*/

/*        ImageCompressorComponent component = DaggerImageCompressorComponent.builder()
                .imageCompressionModule(new ImageCompressionModule())
                .build();

        component.getImageCompressor().compress("http://www.g-widgets.com/GWTcon.jpg");*/
    }
}
