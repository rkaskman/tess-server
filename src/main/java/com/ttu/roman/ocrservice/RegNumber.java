package com.ttu.roman.ocrservice;

public class RegNumber {
    String value;
    int line;
    int wordIndex;

    public RegNumber(String value, int line, int wordIndex) {
        this.value = value;
        this.line = line;
        this.wordIndex = wordIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegNumber regNumber = (RegNumber) o;

        if (!value.equals(regNumber.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
