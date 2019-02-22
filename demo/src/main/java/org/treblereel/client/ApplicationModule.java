package org.treblereel.client;

//import dagger.Module;
//import dagger.Provides;

import javax.inject.Singleton;

//@Module
public class ApplicationModule {

/*    @Provides
    @Singleton
    ImageCompressor provideImageCompressor() {
        return new ImageCompressor();
    }*/

    //@Provides
/*    @Singleton
    ImageDownloader provideImageDownloader() {
        ImageDownloader imageDownloader = new ImageDownloader();
        ApplicationComponent.INSTANCE.inject(imageDownloader);
        return imageDownloader;
    }*/

    //@Provides
    @Singleton
    ImageUploader provideImageUploader() {
        return new ImageUploader();
    }

    //@Provides
    Test provideTest() {
        return new Test();
    }
}
