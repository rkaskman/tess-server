package com.ttu.roman.dao;


import com.ttu.roman.store.Store;

public class StoreMatch {

    private Store store;
    private int numberOfMatches;
    private String totalCost;

    public StoreMatch(Store store, int numberOfMatches) {
        this.store = store;
        this.numberOfMatches = numberOfMatches;
    }

    public int getNumberOfMatches() {
        return numberOfMatches;
    }

    public Store getStore() {
        return store;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}
