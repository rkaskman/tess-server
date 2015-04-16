package com.ttu.roman.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.controller.response.ExpenseResponse;
import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    @Autowired
    ExpenseDAO expenseDAO;

    @RequestMapping(value = "/forUserAndPeriod", method = RequestMethod.POST, consumes = "application/json;")
    @ResponseBody
    public Collection<ExpenseResponse> findExpensesForPeriod(@RequestBody ExpenseRequest expenseRequest) {
        List<Expense> userExpensesForPeriod = expenseDAO.getUserExpensesForPeriod(expenseRequest);
        return Collections2.transform(userExpensesForPeriod, toExpenseResponse());
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
                return response;
            }
        };
    }
}