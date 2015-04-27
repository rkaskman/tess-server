package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.ReceiptImageWrapperDAO;
import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.util.Config;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.postgresql.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

@Component
public class ImagesProcessingComponent {

    private static Logger LOG = Logger.getLogger(ImagesProcessingComponent.class);

    @Autowired
    CompanyRetrievingService companyRetrievingService;
    @Autowired
    ReceiptImageWrapperDAO receiptImageWrapperDAO;
    @Autowired
    Config config;
    @Autowired
    OCRResultHandler ocrResultHandler;

    public void process(ReceiptImageWrapper imageToProcess) throws IOException, ExecutionException, URISyntaxException {
        OcrResultHolder ocrResultHolder = doOcr(imageToProcess);
        if (ocrResultHolder != null) {
            try {
                new OCRResultParser().validateAndFormat(ocrResultHolder);
                String companyName = companyRetrievingService.retrieveCompanyName(ocrResultHolder.regNumber);
                ocrResultHandler.sendValidNotification(imageToProcess, ocrResultHolder, companyName);
            } catch (InvalidOCRResultException | ExecutionException e) {
                handleInvalidOCRResult(ocrResultHolder, e, imageToProcess.getRegistrationId());
            }
        }
    }

    private void handleInvalidOCRResult(OcrResultHolder ocrResultHolder, Exception e, String regId) throws IOException {
        ocrResultHandler.sendErrorNotification(ocrResultHolder, e.getMessage(), regId);
    }

    private OcrResultHolder doOcr(ReceiptImageWrapper imageToProcess) throws IOException {
        File regNumberFile = readAndPreprocess(imageToProcess.getRegNumberPicture(),
                imageToProcess.getRegNumberPictureExtension());
        File totalCostFile = readAndPreprocess(imageToProcess.getTotalCostPicture(),
                imageToProcess.getTotalCostPictureExtension());

        Tesseract1 tesseract = new Tesseract1();
        String regNumberString;
        String totalCostString;

        try {
            regNumberString = tesseract.doOCR(regNumberFile);
            totalCostString = tesseract.doOCR(totalCostFile);
            return new OcrResultHolder(regNumberString, totalCostString);
        } catch (TesseractException e) {
            LOG.error("Images recognition failed", e);
            return null;

        } finally {
            if (regNumberFile != null) {
                regNumberFile.delete();
            }
            if (totalCostFile != null) {
                totalCostFile.delete();
            }
        }
    }

    private File readAndPreprocess(String image, String imageExtension) throws IOException {
        File imageFile = readIntoFile(image,
                imageExtension);
        Mat source = Highgui.imread(imageFile.getAbsolutePath(),
                Highgui.CV_LOAD_IMAGE_COLOR);


        Imgproc.GaussianBlur(source, source, new Size(5, 5), 5, 5);
        Imgproc.cvtColor(source, source , Imgproc.COLOR_BGR2GRAY);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
        Mat temp = new Mat();

        Imgproc.resize(source, temp, new Size(source.cols()/4, source.rows()/4));
        Imgproc.morphologyEx(temp, temp, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.resize(temp, temp, new Size(source.cols(), source.rows()));

        Core.divide(source, temp, temp, 1, CvType.CV_32F); // temp will now have type CV_32F
        Core.normalize(temp, source, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);

        Imgproc.threshold(source, source, -1, 255,
                Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        Mat invertColorMatrix= new Mat(source.rows(),source.cols(), source.type(), new Scalar(255,255,255));

        Core.subtract(invertColorMatrix, source, source);

        File resultImageFile = Files.createTempFile(Paths.get(config.getTempFileDir()), null,
                "." + imageExtension).toFile();
        Highgui.imwrite(resultImageFile.getAbsolutePath(), source);
        imageFile.delete();

        return resultImageFile;
    }

    private File readIntoFile(String encodedImageData, String imageExtension) throws IOException {
        byte[] decodedImageBytes = Base64.decode(encodedImageData);
        File imageFile = Files.createTempFile(Paths.get(config.getTempFileDir()), null,
                "." + imageExtension).toFile();
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(decodedImageBytes);
        fos.close();
        return imageFile;
    }

    public static class OcrResultHolder {
        private String regNumber;
        private String totalCost;

        public String getRegNumber() {
            return regNumber;
        }

        void setRegNumber(String regNumber) {
            this.regNumber = regNumber;
        }

        public String getTotalCost() {
            return totalCost;
        }

        void setTotalCost(String totalCost) {
            this.totalCost = totalCost;
        }

        private OcrResultHolder(String regNumber, String totalCost) {
            this.regNumber = regNumber;
            this.totalCost = totalCost;
        }
    }
}