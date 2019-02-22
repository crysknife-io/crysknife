package org.treblereel.client;

import com.google.gwt.core.client.GWT;

import javax.inject.Inject;

public class ImageDownloader {


    int timeout;

    @Inject
    Test test;

    public ImageDownloader() {
        GWT.log("!ImageDownloader");
        this.timeout = timeout;
    }

    public void download(String url) {
        GWT.log(test.get());
        GWT.log("downloading image at" + url);
    }
}
