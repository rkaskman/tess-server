package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.ocrservice.gcm.GcmNotificationSender;
import com.ttu.roman.service.ExpenseService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;

import static com.ttu.roman.ocrservice.ImagesProcessingComponent.*;
import static com.ttu.roman.service.ExpenseService.CURRENCY_EUR;

@Component
public class OCRResultHandler {


    @Autowired
    GcmNotificationSender gcmNotificationSender;

    @Autowired
    ExpenseDAO expenseDAO;

    public void sendValidNotification(ReceiptImageWrapper receiptImageWrapper, OcrResultHolder ocrResultHolder, String companyName) throws IOException, URISyntaxException {
        Expense result = createExpense(receiptImageWrapper, ocrResultHolder, companyName);
        expenseDAO.create(result);
        GcmNotificationSender.Response response = gcmNotificationSender.sendSuccessfulMessageToCloud(result);
        handleSentNotificationResponse(result, response);
    }

    private void handleSentNotificationResponse(Expense result, GcmNotificationSender.Response response) {
        if (response.getSuccess() == 0 && response.getFailure() == 1) {
            result.setError(response.getError());
        } else if (response.getFailure() == 0 && response.getSuccess() == 1) {
            result.setMessageId(response.getMessageId());
        }
        expenseDAO.update(result);
    }

    private Expense createExpense(ReceiptImageWrapper receiptImageWrapper, OcrResultHolder ocrResultHolder, String companyName) {
        Expense result = new Expense();
        result.setUserId(receiptImageWrapper.getUserId());
        result.setCompanyRegNumber(ocrResultHolder.getRegNumber());
        result.setTotalCost(new BigDecimal(ocrResultHolder.getTotalCost()));
        result.setInsertedAt(new Timestamp(new Date().getTime()));
        result.setCompanyName(companyName);

        //currently eur for now
        result.setCurrency(CURRENCY_EUR);
        result.setRegistrationId(receiptImageWrapper.getRegistrationId());
        result.setState(Expense.STATE_INITIAL);
        return result;
    }

    public void sendErrorNotification(OcrResultHolder ocrResultHolder, String message, String regId) throws IOException {
        gcmNotificationSender.sendErroneousNotification(ocrResultHolder, message, regId);
    }
}