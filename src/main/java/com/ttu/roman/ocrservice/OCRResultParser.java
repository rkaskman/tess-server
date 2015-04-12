package com.ttu.roman.ocrservice;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class OCRResultParser {

    public void validateAndFormat(ImagesProcessingComponent.OcrResultHolder ocrResultHolder) throws InvalidOCRResultException {
        ArrayList<String> errors = new ArrayList<>();

        String formattedRegNumber = getFormattedRegNumber(ocrResultHolder.getRegNumber());
        ocrResultHolder.setRegNumber(formattedRegNumber);

        String formattedTotalCost = getFormattedTotalCost(ocrResultHolder.getTotalCost());
        ocrResultHolder.setTotalCost(formattedTotalCost);
    }

    private String getFormattedRegNumber(String regNumber) throws InvalidOCRResultException {
        String message = "Invalid registration number";
        if (isEmpty(regNumber)) {
            throw new InvalidOCRResultException(message);
        }
        String formattedString = regNumber.replaceAll("\\s+", "").
                replaceAll("[^\\d]", "");

        if (!isEmpty(formattedString) && formattedString.matches("([0-9])+")) {
            return formattedString;
        }

        throw new InvalidOCRResultException(message);
    }

    private String getFormattedTotalCost(String totalCost) throws InvalidOCRResultException {
        String message = "Invalid total cost number";
        if (isEmpty(totalCost)) {
            throw new InvalidOCRResultException(message);
        }
        String formattedString = totalCost.replaceAll("\\s+", "").replaceAll(",", ".").
                replaceAll("[^\\d.]", "");

        if (!isEmpty(formattedString) && formattedString.matches("\\d+\\.\\d{2}")) {
            return formattedString;
        }

        throw new InvalidOCRResultException(message);
    }
}