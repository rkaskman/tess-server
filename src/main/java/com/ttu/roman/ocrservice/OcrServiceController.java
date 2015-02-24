package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.StoreDao;
import com.ttu.roman.dao.StoreMatch;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class OcrServiceController {
//    @Autowired
//    OCRTotalCostParser ocrResultParser;
//    @Autowired
//    StoreDao storeDao;
//
//    @RequestMapping(value = "/postFileForOcr", method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
//    public
//    @ResponseBody
//    String parseResult(InputStream in) throws IOException {
//        File file = readInputStreamIntoFile(in);
//        BufferedImage image = ImageIO.read(file);
//
//        RescaleOp rescaleOp = new RescaleOp(0.8f, 0, null);
//        rescaleOp.filter(image, image);
//        String ocrResult = performOCR(image);
//
//        StoreMatch parsedResult = ocrResultParser.parse(ocrResult);
//
//        file.delete();
//        return parsedResult.getTotalCost();
//    }
//
//    private File readInputStreamIntoFile(InputStream in) throws IOException {
//        File file = new File("file.jpg");
//        FileOutputStream out = new FileOutputStream(file);
//
//        byte[] buf = new byte[16384];
//        int len = in.read(buf);
//        while (len != -1) {
//            out.write(buf, 0, len);
//            len = in.read(buf);
//        }
//        out.close();
//        return file;
//    }
//
//    private String performOCR(BufferedImage image) {
//        Tesseract instance = Tesseract.getInstance();
//
//        try {
//            String s = instance.doOCR(image);
//            return s;
//        } catch (TesseractException e) {
//            System.err.println(e.getMessage());
//        }
//        return "";
//    }
}