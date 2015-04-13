package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
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
    ExpenseDAO expenseDAO;
    public void sendValidNotification(ReceiptImageWrapper receiptImageWrapper, OcrResultHolder ocrResultHolder, String companyName) {
        Expense result = createExpense(receiptImageWrapper, ocrResultHolder, companyName);
        expenseDAO.create(result);
        //TODO: send push notification on success

    }

    private Expense createExpense(ReceiptImageWrapper receiptImageWrapper, OcrResultHolder ocrResultHolder, String companyName) {
        Expense result = new Expense();
        result.setUserId(receiptImageWrapper.getUserId());
        result.setCompanyRegNumber(ocrResultHolder.getRegNumber());
        result.setTotalCost(new BigDecimal(ocrResultHolder.getTotalCost()));
        result.setInsertedAt(new Timestamp(new Date().getTime()));
        result.setCompanyName(companyName);
        result.setRegistrationId(receiptImageWrapper.getRegistrationId());
        result.setState(Expense.STATE_INITIAL);
        return result;
    }

    public void sendErrorNotification(ReceiptImageWrapper imageToProcess, String message) {
        //TODO: add erroneous push notification
    }
}