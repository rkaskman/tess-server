package com.ttu.roman.ocrservice;

public class ImagesWrapper {
    public ImageWrapper receiptImage;
    public String registrationId;

    public static class ImageWrapper {
        public String encodedImage;
        public String fileExtension;
    }
}