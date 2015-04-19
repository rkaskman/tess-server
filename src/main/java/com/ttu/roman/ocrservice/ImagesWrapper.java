package com.ttu.roman.ocrservice;

public class ImagesWrapper {
    public ImageWrapper regNumberImage;
    public ImageWrapper totalCostImage;
    public String registrationId;

    public static class ImageWrapper {
        public String encodedImage;
        public String fileExtension;
    }
}