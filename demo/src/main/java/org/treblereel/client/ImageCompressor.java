package org.treblereel.client;

import com.google.gwt.core.client.GWT;

import javax.inject.Inject;

public class ImageCompressor {

    @Inject
    public ImageDownloader downloader;

    @Inject
    public ImageUploader uploader;

    public ImageCompressor(){
            GWT.log("RUN");
    }

    public void compress(String url) {
        downloader.download(url);
        GWT.log("compressing image");
        uploader.upload(url);
    }
}
