package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.ReceiptImageWrapperDAO;
import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.util.Config;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.log4j.Logger;
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
                handleInvalidOCRResult(ocrResultHolder,  e, imageToProcess.getRegistrationId());
            }
        }
    }

    private void handleInvalidOCRResult(OcrResultHolder ocrResultHolder, Exception e, String regId) throws IOException {
        ocrResultHandler.sendErrorNotification(ocrResultHolder, e.getMessage(), regId);
    }

    private OcrResultHolder doOcr(ReceiptImageWrapper imageToProcess) throws IOException {
        File regNumberFile = readIntoFile(imageToProcess.getRegNumberPicture(),
                imageToProcess.getRegNumberPictureExtension());
        File totalCostFile = readIntoFile(imageToProcess.getTotalCostPicture(),
                imageToProcess.getTotalCostPictureExtension());

        Tesseract1 tesseract = new Tesseract1();
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789.");
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