package com.ttu.roman.controller;


import com.ttu.roman.controller.request.ExpenseInput;
import com.ttu.roman.controller.response.ExpenseResponseContainer.ExpenseResponse;
import com.ttu.roman.service.ExpenseService;
import com.ttu.roman.service.exception.InvalidExpenseException;
import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.controller.response.ExpenseResponseContainer;
import com.ttu.roman.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        expenseService.confirmExpense(Long.parseLong(recognitionId));
    }

    @RequestMapping(value = "/decline/{recognitionId}", method = RequestMethod.POST)
    @ResponseBody
    public void decline(@PathVariable String recognitionId) throws InvalidExpenseException {
        expenseService.deleteExpense(Long.parseLong(recognitionId));
    }

    @RequestMapping(value = "/submitManually", method = RequestMethod.POST, consumes = "application/json;")
    @ResponseBody
    public ExpenseResponse submitManually(@RequestBody ExpenseInput expenseInput) throws InvalidExpenseException {
        return expenseService.saveInitialExpense(expenseInput);
    }

}