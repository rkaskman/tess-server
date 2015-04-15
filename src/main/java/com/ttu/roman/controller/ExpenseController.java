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
import java.util.*;

@Controller("/expense")
public class ExpenseController {

    @Autowired
    ExpenseDAO expenseDAO;

    @RequestMapping(value = "/forUserAndPeriod", method = RequestMethod.GET, consumes = "application/json;")
    @ResponseBody
    public Collection<ExpenseResponse> findExpensesForPeriod(@RequestBody ExpenseRequest expenseRequest) {
        List<Expense> userExpensesForPeriod = expenseDAO.getUserExpensesForPeriod(expenseRequest);
        return Collections2.transform(userExpensesForPeriod, new Function<Expense, ExpenseResponse>() {
            @Nullable
            @Override
            public ExpenseResponse apply(Expense expense) {
                ExpenseResponse response = new ExpenseResponse();
                response.id = expense.getId();
                response.companyName = expense.getCompanyName();
                response.date = new Date(expense.getInsertedAt().getTime());
                response.sum = expense.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                return response;
            }
        });
    }
}
