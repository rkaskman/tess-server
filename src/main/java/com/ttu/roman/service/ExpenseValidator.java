package com.ttu.roman.service;

import com.ttu.roman.model.Expense;
import com.ttu.roman.service.exception.InvalidExpenseException;
import org.springframework.stereotype.Component;

import static com.ttu.roman.util.Util.getAuthenticatedUser;

@Component
public class ExpenseValidator {

     void validateStateInitial(Expense expense) throws InvalidExpenseException {
        if(!Expense.STATE_INITIAL.equals(expense.getState())) {
            throw new InvalidExpenseException("invalidStateException");
        }
    }

     void validateUser(Expense expense) throws InvalidExpenseException {
        if(!getAuthenticatedUser().getGoogleUserId().equals(expense.getUserId())) {
            throw new InvalidExpenseException("invalidUser");
        }
    }

    void assertExpenseNotNull(Expense expense) throws InvalidExpenseException {
        if(expense == null) {
            throw new InvalidExpenseException("expenseDoesNotExist");
        }
    }
}
