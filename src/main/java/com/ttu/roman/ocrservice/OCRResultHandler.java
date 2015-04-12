package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.ProcessedImageDAO;
import com.ttu.roman.model.ProcessedImageResult;
import com.ttu.roman.model.ReceiptImageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static com.ttu.roman.ocrservice.ImagesProcessingComponent.*;

@Component
public class OCRResultHandler {

    @Autowired
    ProcessedImageDAO processedImageDAO;
    public void sendValidNotification(ReceiptImageWrapper receiptImageWrapper, OcrResultHolder ocrResultHolder, String companyName) {
        ProcessedImageResult result = createProcessImageResult(receiptImageWrapper, ocrResultHolder, companyName);
        processedImageDAO.create(result);
        //TODO: send push notification on success

    }

    private ProcessedImageResult createProcessImageResult(ReceiptImageWrapper receiptImageWrapper, OcrResultHolder ocrResultHolder, String companyName) {
        ProcessedImageResult result = new ProcessedImageResult();
        result.setUserId(receiptImageWrapper.getUserId());
        result.setCompanyRegNumber(ocrResultHolder.getRegNumber());
        result.setTotalCost(new BigDecimal(ocrResultHolder.getTotalCost()));
        result.setInsertedAt(new Timestamp(new Date().getTime()));
        result.setCompanyName(companyName);
        result.setRegistrationId(receiptImageWrapper.getRegistrationId());
        result.setState(ProcessedImageResult.STATE_INITIAL);
        return result;
    }

    public void sendErrorNotification(ReceiptImageWrapper imageToProcess, String message) {
        //TODO: add erroneous push notification
    }
}