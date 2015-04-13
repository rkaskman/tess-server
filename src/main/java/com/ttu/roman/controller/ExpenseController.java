package com.ttu.roman.controller;

import com.ttu.roman.dao.ExpenseDAO;
import com.ttu.roman.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller("/expense")
public class ExpenseController {

    @Autowired
    ExpenseDAO expenseDAO;

    @RequestMapping("/forUserAndPeriod")
    public Collection<Expense> findExpensesForPeriod() {

    return null;
    }
}
