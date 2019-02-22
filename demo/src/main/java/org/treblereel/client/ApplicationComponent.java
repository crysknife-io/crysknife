package org.treblereel.client;


//import dagger.Component;

import javax.inject.Singleton;

@Singleton
@SuppressWarnings("unused")
//@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

/*    ApplicationComponent INSTANCE = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule())
            .build();*/

    ImageCompressor imageCompressor();

    void inject(ImageDownloader imageDownloader);
    void inject(ImageCompressor imageCompressor);

}
