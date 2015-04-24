package com.ttu.roman.controller.response;

import java.util.Collection;

public class ExpenseResponseContainer {
    public Collection<ExpenseResponse> expenseList;
    public boolean lastReached;

    public static class ExpenseResponse {
        public long id;
        public String companyName;
        public String sum;
        public String date;
        public String state;
        public String currency;
    }
}
