package org.example;

import java.util.List;

public interface IAnalyzer {
    boolean isAutomatDescriptionCorrect(List<String> inputLines);
    boolean isDescriptionLineCorrect(String analyzingLine);
    String[] readState(String analyzingLine, int index);
}