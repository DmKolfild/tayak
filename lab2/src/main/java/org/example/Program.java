package org.example;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Program {
    private static final String directory = Paths.get("").toAbsolutePath().resolve("D:\\ТАЯК\\tayak\\lab2\\src\\main\\java\\org\\example\\").toString();
    private static final List<Character> alphabet = new ArrayList<>() {
        {
            add('\\');
            add('/');
            add('a');
            add('+');
            add('d');
            add('\"');
            add('c');
            add('e');
            add('f');
            add('g');
            add('b');
            add('8');
            add('*');
            add('0');
            add('1');
            add(';');
            add('*');
            add(',');
            add(' ');
            add('=');
            add('3');
            add('5');
            add('7');
        }
    };

    public static void main(String[] args) throws FileNotFoundException {
        FileManager fileManager = new FileManager(Paths.get(directory, "var3_nd.txt").toString());

        fileManager.readFileByLines();

        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(alphabet);
        if (!codeAnalyzer.isAutomatDescriptionCorrect(fileManager.getFileLines())) {
            return;
        }

        Automate automate = new Automate(fileManager.getFileLines(), alphabet);

        if (automate.IsAutomateDeterministic()) {
            System.out.println("Automate is Deterministic.\n");
        } else {
            System.out.println("Automate is not Deterministic.\n");
        }

        automate.PrintTransitionFunctions();
        automate.determization();
        automate.PrintTransitionFunctions();

        if (automate.isExecutableForInputLine("aaaaaaaaab")) {
            System.out.println("Is executable");
        } else {
            System.out.println("Is NOT executable");
        }
    }
}