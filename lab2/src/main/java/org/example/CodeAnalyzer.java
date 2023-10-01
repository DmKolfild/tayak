package org.example;

import java.util.List;

public class CodeAnalyzer implements IAnalyzer {
    private final List<Character> alphabet;

    public CodeAnalyzer(List<Character> alphabet) {
        this.alphabet = alphabet;
    }

    public boolean isAutomatDescriptionCorrect(List<String> inputLines) {
        for (String line : inputLines) {
            if (!isDescriptionLineCorrect(line)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDescriptionLineCorrect(String analyzingLine) {
        int i = 0;

        i = Integer.parseInt(readState(analyzingLine, i)[1]);

        if (analyzingLine.length() == i) {
            System.out.println("Incorrect syntax. Enter ',' and symbol of transmission and output state. ");
            return false;
        }

        if (analyzingLine.charAt(i) != ',') {
            System.out.println("1");
            System.out.println("Incorrect symbol - " + analyzingLine.charAt(i));
            return false;
        }
        i++;

        if (analyzingLine.length() == i) {
            System.out.println("Incorrect syntax. Enter symbol of transmission and output state.");
            return false;
        }

        if (!alphabet.contains(analyzingLine.charAt(i))) {
            System.out.println("2");
            System.out.println("Incorrect symbol - " + analyzingLine.charAt(i));
            return false;
        }
        i++;

        if (analyzingLine.length() == i) {
            System.out.println("Incorrect syntax. Enter '=state'");
            return false;
        }

        if (analyzingLine.charAt(i) != '=') {
            System.out.println("3");
            System.out.println("Incorrect symbol - " + analyzingLine.charAt(i));
            return false;
        }
        i++;

        if (analyzingLine.length() == i) {
            System.out.println("Incorrect syntax. Enter output state");
            return false;
        }

        i = Integer.parseInt(readState(analyzingLine, i)[1]);

        if (i < analyzingLine.length()) {
            System.out.println("4");
            System.out.println("Incorrect symbol - " + analyzingLine.charAt(i));
            return false;
        }

        return true;
    }

    public String[] readState(String analyzingLine, int index) {
        StringBuilder state = new StringBuilder();
        while (index < analyzingLine.length() && (Character.isDigit(analyzingLine.charAt(index)) || Character.isLetter(analyzingLine.charAt(index)))) {
            state.append(analyzingLine.charAt(index));
            index++;
        }
        String[] result = new String[2];
        result[0] = state.toString();
        result[1] = String.valueOf(index);
        return result;
    }
}