package com.ttu.roman.service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.ttu.roman.auth.User;
import com.ttu.roman.controller.request.ExpenseInput;
import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.controller.response.ExpenseResponseContainer;
import com.ttu.roman.controller.response.ExpenseResponseContainer.ExpenseResponse;
import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.ocrservice.CompanyNotFoundException;
import com.ttu.roman.ocrservice.CompanyRetrievingService;
import com.ttu.roman.ocrservice.ImagesProcessingComponent;
import com.ttu.roman.service.exception.InvalidExpenseException;
import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
import com.ttu.roman.util.Config;
import com.ttu.roman.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ExpenseService {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static final String CURRENCY_EUR = "EUR";

    @Autowired
    ExpenseDAO expenseDAO;
    @Autowired
    Config config;
    @Autowired
    ExpenseValidator expenseValidator;
    @Autowired
    CompanyRetrievingService companyRetrievingService;

    public void confirmExpense(Long expenseId) throws InvalidExpenseException {
        Expense expense = expenseDAO.find(expenseId);

        validateExpense(expense);
        expense.setState(Expense.STATE_ACCEPTED);
        expenseDAO.update(expense);
    }

    public void deleteExpense(Long expenseId) throws InvalidExpenseException {
        Expense expense = expenseDAO.find(expenseId);
        validateExpense(expense);
        expenseDAO.delete(expense);
    }

    private void validateExpense(Expense expense) throws InvalidExpenseException {
        expenseValidator.assertExpenseNotNull(expense);
        expenseValidator.validateUser(expense);
        expenseValidator.validateStateInitial(expense);
    }

    public ExpenseResponseContainer findExpensesForPeriod(ExpenseRequest expenseRequest) {
        List<Expense> userExpensesForPeriod = expenseDAO.getUserExpensesForPeriod(expenseRequest);

        ExpenseResponseContainer expenseResponseContainer = new ExpenseResponseContainer();
        expenseResponseContainer.expenseList = Collections2.transform(userExpensesForPeriod, toExpenseResponse());
        expenseResponseContainer.lastReached = userExpensesForPeriod.size() < config.getMaxExpensesResultAtOnce();

        return expenseResponseContainer;
    }

    private static Function<Expense, ExpenseResponse> toExpenseResponse() {
        return new Function<Expense, ExpenseResponse>() {
            @Nullable
            @Override
            public ExpenseResponse apply(Expense expense) {
                ExpenseResponse response = new ExpenseResponse();
                response.id = expense.getId();
                response.companyName = expense.getCompanyName();

                response.date = SIMPLE_DATE_FORMAT.format(new Date(expense.getInsertedAt().getTime()));
                response.sum = expense.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                response.regNumber = expense.getCompanyRegNumber();
                response.state = expense.getState();
                response.currency = expense.getCurrency();
                return response;
            }
        };
    }

    private Expense createExpenseFrom(ExpenseInput expenseInput) throws CompanyNotFoundException {
        User user = Util.getAuthenticatedUser();

        Expense expense = new Expense();
        expense.setUserId(user.getGoogleUserId());
        expense.setCompanyRegNumber(expenseInput.regNumber);
        expense.setTotalCost(expenseInput.totalCost);
        expense.setInsertedAt(new Timestamp(new Date().getTime()));
        String companyName;
        try {
            companyName = companyRetrievingService.retrieveCompanyName(expenseInput.regNumber);
        } catch (Exception e) {
            throw new CompanyNotFoundException("companyRetrievalError");
        }
        expense.setCompanyName(companyName);

        //currently eur for now
        expense.setCurrency(CURRENCY_EUR);
        expense.setState(Expense.STATE_INITIAL);
        return expense;
    }

    public ExpenseResponse saveInitialExpense(ExpenseInput expenseInput) throws CompanyNotFoundException {
        Expense expense = createExpenseFrom(expenseInput);
        expenseDAO.create(expense);
        return toExpenseResponse().apply(expense);
    }
}
