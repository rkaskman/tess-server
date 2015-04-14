package com.ttu.roman.controller.request;

import java.util.Date;

public class ExpenseRequest {
    public String userId;
    public Date startDate;
    public Date endDate;
    public int lastId = 0;
    public int maxResults;
}
