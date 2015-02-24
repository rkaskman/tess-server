package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.StoreMatch;
import com.ttu.roman.service.OcrDataMatchException;
import com.ttu.roman.service.StoreReceiptMatcherService;
import com.ttu.roman.store.Store;
import net.java.frej.Regex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class OCRTotalCostParser {

    @Autowired
    StoreReceiptMatcherService storeReceiptMatcherService;

    public StoreMatch parse(String input) {
        input = input.replaceAll("\n", " ").replaceAll("\\s+", " ").replaceAll(",", ".").toLowerCase();

        Collection<StoreMatch> matchingStores;
        try {
            matchingStores = storeReceiptMatcherService.getMatchingStores(input);
        } catch (OcrDataMatchException e) {
            //todo: logging
            return null;
        }

        String[] tokens = input.split(" ");
        for (StoreMatch storeMatch : matchingStores) {
           int currentTokenIndex = 0;

            Regex regex = new Regex("[^(" + storeMatch.getStore().getTotalSumTag() + ")]");
            String partContainingTotalCost;
            for (String token : tokens) {
                if(regex.match(token)) {
                   partContainingTotalCost = StringUtils.join(tokens, " ",  currentTokenIndex+1, tokens.length -1);
                    String totalCost = parseTotalSum(partContainingTotalCost);
                    if(totalCost != null) {
                        storeMatch.setTotalCost(totalCost);
                        return storeMatch;
                    }
                }
                currentTokenIndex++;
            }
        }

        return null;
    }

    private String parseTotalSum(String partContainingTotalCost) {
        StringBuilder sb = new StringBuilder();
        boolean totalCostNumStarted = false;
        for (int i = 0; i < partContainingTotalCost.length(); i++) {
            char c = partContainingTotalCost.charAt(i);
            if (c >= '0' && c <= '9') {
                totalCostNumStarted = true;
                sb.append(c);
            } else if (totalCostNumStarted) {
                if ((c == '.' || c == ',')) {
                    sb.append(c);
                } else if (c == ' ') {
                    continue;
                } else {
                    break;
                }
            }
        }
        String result = sb.toString();
        if (result.matches("[0-9]+\\.[0-9]{2}")) {
            return result;
        }
        return null;
    }
}
