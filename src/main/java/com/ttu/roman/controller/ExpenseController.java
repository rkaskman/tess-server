package com.ttu.roman.controller;


import com.ttu.roman.service.ExpenseService;
import com.ttu.roman.service.exception.InvalidExpenseException;
import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.controller.response.ExpenseResponseContainer;
import com.ttu.roman.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @RequestMapping(value = "/forUserAndPeriod", method = RequestMethod.POST, consumes = "application/json;")
    @ResponseBody
    public ExpenseResponseContainer findExpensesForPeriod(@RequestBody ExpenseRequest expenseRequest) {
       return expenseService.findExpensesForPeriod(expenseRequest);
    }

    @RequestMapping(value = "/confirm/{recognitionId}", method = RequestMethod.POST)
    @ResponseBody
    public void confirm(@PathVariable String recognitionId) throws InvalidExpenseException {
        expenseService.updateExpenseState(Long.parseLong(recognitionId), Expense.STATE_ACCEPTED);
    }

    @RequestMapping(value = "/decline/{recognitionId}", method = RequestMethod.POST)
    @ResponseBody
    public void decline(@PathVariable String recognitionId) throws InvalidExpenseException {
        expenseService.updateExpenseState(Long.parseLong(recognitionId), Expense.STATE_DECLINED);
    }
}