package com.ttu.roman.ocrservice;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.postgresql.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/ocr")
public class OcrServiceController {

    @RequestMapping(value = "/postImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String postImage(@RequestBody ImagesWrapper imagesWrapper) throws IOException, RecognitionException {
        File regCodeFile = readIntoFile(imagesWrapper.regNumberImage);
        File totalCostFile = readIntoFile(imagesWrapper.totalCostImage);

        Tesseract tesseract = Tesseract.getInstance();

        try {
            String regCodeOcrString = tesseract.doOCR(regCodeFile);
            String totalCostOcrString = tesseract.doOCR(totalCostFile);
//            TODO: decide if to do ocr immediately or asynchronously

        } catch (TesseractException e) {
            throw new RecognitionException("Failed to recognize from photo");
            //TODO: logging
        } finally {
            regCodeFile.delete();
            totalCostFile.delete();
        }


        return "bep";
    }

    private File readIntoFile(ImagesWrapper.ImageWrapper imageWrapper) throws IOException {
        byte[] decodedImageBytes = Base64.decode(imageWrapper.encodedImage);
        File imageFile = new File(imageWrapper.fileName);
        FileOutputStream fos = new FileOutputStream(imageWrapper.fileName);
        fos.write(decodedImageBytes);
        fos.close();
        return imageFile;
    }
}