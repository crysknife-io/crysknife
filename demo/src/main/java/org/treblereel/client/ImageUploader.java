package org.treblereel.client;

import com.google.gwt.core.client.GWT;

import javax.inject.Inject;

public class ImageUploader {

    public ImageUploader() {
        GWT.log("ImageUploader");
    }

    public void upload(String url) {
        GWT.log("uploading compresesed image at " + url);
    }
}
