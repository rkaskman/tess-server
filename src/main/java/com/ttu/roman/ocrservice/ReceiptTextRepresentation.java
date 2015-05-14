package com.ttu.roman.ocrservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ReceiptTextRepresentation {

    private List<TextLine> textLines = new ArrayList<>();

    public List<TextLine> getTextLines() {
        return Collections.unmodifiableList(textLines);
    }

    public void addTextLines(List<TextLine> lines) {
        textLines.addAll(lines);
    }

    public static class TextLine {
        private List<String> words = new ArrayList<>();

        public void add(String[] words) {
            this.words.addAll(Arrays.asList(words));
        }

        public List<String> getWords() {
            return Collections.unmodifiableList(words);
        }
    }

}
