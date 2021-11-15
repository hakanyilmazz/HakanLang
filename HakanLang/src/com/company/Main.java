package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> variables = new HashMap<>();
    private static String enteredText = "";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("*** HakanLang Programming Language ***");
            String code = "";

            while (!code.equals("-1")) {
                System.out.print("||(-1 for EXIT) >>> ");
                code = scanner.nextLine();
                runCode(code);
            }
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    runCode(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                scanner.close();
            }
        }
    }

    private static void runCode(String line) {
        if (line.startsWith("?") || line.equals("")) {
            return;
        }

        if (line.startsWith("-")) {
            Object foundedValue = variables.get(line.substring(1));
            System.out.println(foundedValue);
            return;
        }

        if (line.startsWith(DefaultFunctions.display.name())) {
            display(DefaultFunctions.display, line);
        } else if (line.startsWith(DefaultFunctions.enterLine.name())) {
            enteredText = enterLine(line);
        } else if (line.startsWith("#")) {
            createVariable(line);
        } else if (line.startsWith(DefaultMathOperations.add.name())) {
            displayMathOperation(DefaultMathOperations.add, line);
        } else if (line.startsWith(DefaultMathOperations.sub.name())) {
            displayMathOperation(DefaultMathOperations.sub, line);
        } else if (line.startsWith(DefaultMathOperations.mul.name())) {
            displayMathOperation(DefaultMathOperations.mul, line);
        } else if (line.startsWith(DefaultMathOperations.div.name())) {
            displayMathOperation(DefaultMathOperations.div, line);
        } else if (line.startsWith(DefaultMathOperations.mod.name())) {
            displayMathOperation(DefaultMathOperations.mod, line);
        }
    }

    private static void displayMathOperation(DefaultMathOperations operationType, String line) {
        String[] strNumber = line.split(" ");

        if (strNumber.length == 2) {
            display(DefaultFunctions.displayMathResult, strNumber[1]);
            return;
        }

        int res = Integer.parseInt(strNumber[1]);

        for (int i = 1; i < strNumber.length - 1; i++) {
            switch (operationType) {
                case add -> res += Integer.parseInt(strNumber[i + 1]);
                case sub -> res -= Integer.parseInt(strNumber[i + 1]);
                case mul -> res *= Integer.parseInt(strNumber[i + 1]);
                case div -> res /= Integer.parseInt(strNumber[i + 1]);
                case mod -> res %= Integer.parseInt(strNumber[i + 1]);
            }
        }

        display(DefaultFunctions.displayMathResult, String.valueOf(res));
    }

    private static void createVariable(String line) {
        String variableName = line.substring(1, line.indexOf("=")).trim();

        if (line.contains(DefaultFunctions.enterLine.name())) {
            line = line.substring(line.indexOf(DefaultFunctions.enterLine.name()));
            variables.put(variableName, enterLine(line));
            return;
        }

        String value = line.substring(line.indexOf("=") + 1).trim();

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        } else if (value.startsWith("-")) {
            String foundedVariableName = value.substring(value.indexOf("-") + 1, value.indexOf("."));
            boolean isContainsVariable = variables.containsKey(foundedVariableName);

            if (isContainsVariable) {
                Object foundedValue = variables.get(foundedVariableName);

                if (foundedValue instanceof String str) {
                    String methodName = value.substring(value.indexOf(".") + 1, value.length() - 2);
                    value = getStrFromStringMethod(str, methodName);
                }
            }
        }

        variables.put(variableName, value);
    }

    private static String getStrFromStringMethod(String str, String methodName) {
        switch (methodName) {
            case "length" -> {
                return String.valueOf(str.length());
            }

            case "toLowerCase" -> {
                return str.toLowerCase();
            }

            case "toUpperCase" -> {
                return str.toUpperCase();
            }
        }

        return str;
    }

    private static String enterLine(String line) {
        display(DefaultFunctions.enterLine, line);
        return scanner.nextLine();
    }

    private static void display(DefaultFunctions functionType, String line) {
        String text = "";

        switch (functionType) {
            case display -> {
                final String value = line.substring(DefaultFunctions.display.name().length() + 1, line.length() - 1);

                if (value.startsWith("\"")) {
                    text = value.substring(1, value.length() - 1);

                    if (text.contains("${")) {
                        String constantText = text.substring(0, text.indexOf("${"));
                        String tempText = text.substring(text.indexOf("${") + 2, text.indexOf("}"));
                        if (tempText.startsWith("-")) {
                            String variableName = tempText.substring(1);
                            text = constantText + variables.get(variableName);
                        }
                    }
                } else if (value.startsWith("-")) {
                    String variableName = value.substring(1);
                    text = variables.get(variableName);
                }

                text += "\n";
            }
            case enterLine -> text = line.substring(DefaultFunctions.enterLine.name().length() + 2, line.length() - 2);
            case displayMathResult -> text = line + "\n";
        }

        System.out.print(text);
    }

    private enum DefaultFunctions {
        display, enterLine, displayMathResult
    }

    private enum DefaultMathOperations {
        add, sub, mul, div, mod
    }
}
