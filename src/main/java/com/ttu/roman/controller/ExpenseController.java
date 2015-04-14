package com.ttu.roman.controller;

import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller("/expense")
public class ExpenseController {

    @Autowired
    ExpenseDAO expenseDAO;

    @RequestMapping(value = "/forUserAndPeriod", method = RequestMethod.POST, consumes = "application/json;")
    @ResponseBody
    public Collection<Expense> findExpensesForPeriod(@RequestBody ExpenseRequest expenseRequest) {
        return expenseDAO.getUserExpensesForPeriod(expenseRequest);
    }
}
