package com.ttu.roman.service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.ttu.roman.auth.User;
import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.controller.response.ExpenseResponseContainer;
import com.ttu.roman.service.exception.InvalidExpenseException;
import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
import com.ttu.roman.util.Config;
import com.ttu.roman.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ExpenseService {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Autowired
    ExpenseDAO expenseDAO;
    @Autowired
    Config config;

    public void updateExpenseState(Long expenseId, String state) throws InvalidExpenseException {
        Expense expense = expenseDAO.find(expenseId);
        if (expense != null) {
            User user = Util.getAuthenticatedUser();
            if (expense.getUserId().equals(user.getGoogleUserId())) {
                expense.setState(state);
                expenseDAO.update(expense);
            } else {
                throw new InvalidExpenseException("Expense does not belong to the user");
            }
        } else {
            throw new InvalidExpenseException("Expense does not exist");
        }
    }

    public ExpenseResponseContainer findExpensesForPeriod(ExpenseRequest expenseRequest) {
        List<Expense> userExpensesForPeriod = expenseDAO.getUserExpensesForPeriod(expenseRequest);

        ExpenseResponseContainer expenseResponseContainer = new ExpenseResponseContainer();
        expenseResponseContainer.expenseList = Collections2.transform(userExpensesForPeriod, toExpenseResponse());
        expenseResponseContainer.lastReached = userExpensesForPeriod.size() < config.getMaxExpensesResultAtOnce();

        return expenseResponseContainer;
    }

    private static Function<Expense, ExpenseResponseContainer.ExpenseResponse> toExpenseResponse() {
        return new Function<Expense, ExpenseResponseContainer.ExpenseResponse>() {
            @Nullable
            @Override
            public ExpenseResponseContainer.ExpenseResponse apply(Expense expense) {
                ExpenseResponseContainer.ExpenseResponse response = new ExpenseResponseContainer.ExpenseResponse();
                response.id = expense.getId();
                response.companyName = expense.getCompanyName();

                response.date = SIMPLE_DATE_FORMAT.format(new Date(expense.getInsertedAt().getTime()));
                response.sum = expense.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                return response;
            }
        };
    }
}
