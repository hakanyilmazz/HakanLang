package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> variables = new HashMap<>();
    private static final Functions functions = new Functions();
    private static String enteredText = "";
    private static boolean isFile = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("********** HakanLang Programming Language **********");
            String code = "";

            while (!code.equals("-1")) {
                try {
                    System.out.print("||(-1 for EXIT) >>> ");
                    code = scanner.nextLine();

                    if (code.startsWith("f-") && code.endsWith("{")) {
                        String detail = "";
                        code += "\n";
                        StringBuilder lineBuilder = new StringBuilder(code);

                        while (!detail.endsWith("}")) {
                            detail = scanner.nextLine();
                            lineBuilder.append(detail).append("\n");
                        }

                        code = lineBuilder.toString();
                    }

                    runCode(code);

                } catch (Exception e) {
                    System.out.println("Code exception!");
                }
            }
        } else {
            isFile = true;

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))) {
                String line;
                StringBuilder functionCode = new StringBuilder();
                boolean isFunction = false;

                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        if (line.startsWith("f-") && line.endsWith("{")) {
                            functionCode.append(line).append("\n");
                            isFunction = true;
                            continue;
                        }

                        if (line.endsWith("}")) {
                            isFunction = false;
                            functionCode.append(line).append("\n");
                            line = functionCode.toString();
                            functionCode = new StringBuilder();
                        }

                        if (isFunction) {
                            functionCode.append(line).append("\n");
                            continue;
                        }

                        runCode(line.trim());
                    } catch (Exception e) {
                        System.out.println("Code exception!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    private static void runCode(String line) {
        line = line.trim();

        if (line.startsWith("?") || line.equals("")) {
            return;
        }

        if (line.startsWith("##") && !isFile) {
            System.out.println(line.substring(2));
            return;
        }

        if (line.startsWith("for")) {
            createForLoop(line);
            return;
        }

        if (line.startsWith("-")) {
            String foundedValue = variables.get(line.substring(1));

            if (line.contains(".")) {
                foundedValue = variables.get(line.substring(1, line.indexOf(".")));
                String methodName = line.substring(line.indexOf(".") + 1);
                System.out.println(getStrFromStringMethod(foundedValue, methodName));
                return;
            }

            System.out.println(foundedValue);

            return;
        }

        if (line.startsWith("f-")) {
            if (line.endsWith("{")) {
                String detail = "";
                line += "\n";
                StringBuilder lineBuilder = new StringBuilder(line);

                while (!detail.endsWith("}")) {
                    detail = scanner.nextLine();
                    lineBuilder.append(detail).append("\n");
                }

                line = lineBuilder.toString();
            }

            functions.createFunction(line);
            return;
        } else if (line.startsWith("~")) {
            display(DefaultFunctions.runFunction, functions.runFunction(line));
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

    private static void createForLoop(String line) {
        if (!line.startsWith("for")) {
            return;
        }

        String forArguments = line.substring(line.indexOf("->") + 2).trim();
        String iteratedLine = line.substring(line.lastIndexOf("->") + 2).trim();

        String[] arguments = forArguments.split(" ");
        String strNumbers = arguments[0];

        for (int i = Integer.parseInt(strNumbers.substring(0, strNumbers.indexOf(".")));
             i <= Integer.parseInt(strNumbers.substring(strNumbers.lastIndexOf(".") + 1));
             ++i) {
            createVariable("#i = " + i);
            runCode(iteratedLine);
        }

        variables.remove("i");
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
                    String methodName = value.substring(value.indexOf(".") + 1);
                    value = getStrFromStringMethod(str, methodName);
                }
            }
        } else if (value.startsWith("~")) {
            String code = functions.runFunction(value);
            display(DefaultFunctions.runFunction, code);
            value = value.substring(value.indexOf("~") + 1, value.indexOf("("));
            String result = functions.getReturnValue(value);
            if (result.startsWith("##")) {
                value = result.substring(2);
            }
        }

        variables.put(variableName, value);
    }

    private static String getStrFromStringMethod(String str, String methodName) {
        if (str.startsWith("-")) {
            String value = variables.get(str.substring(1));
            if (value != null) {
                str = value;
            }
        }

        String parameter = methodName.substring(methodName.indexOf("(") + 1, methodName.indexOf(")"));

        if (parameter.startsWith("-")) {
            String value = variables.get(parameter.substring(1));
            if (value != null) {
                parameter = value;
            }
        }

        methodName = methodName.substring(0, methodName.indexOf("("));

        Method method;
        if (!parameter.isEmpty()) {
            try {
                method = str.getClass().getMethod(methodName, Object.class);
                return String.valueOf(method.invoke(str, parameter));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            method = str.getClass().getMethod(methodName);
            return String.valueOf(method.invoke(str));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
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
                        } else if (tempText.startsWith("~")) {
                            runCode(tempText);
                            tempText = tempText.substring(tempText.indexOf("~") + 1, tempText.indexOf("("));
                            String result = functions.getReturnValue(tempText).substring(2);
                            text = constantText + result;
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
            case runFunction -> {
                String[] lines = line.split("\n");
                for (String codeLine : lines) {
                    runCode(codeLine);
                }
                return;
            }
        }

        System.out.print(text);
    }

    private enum DefaultFunctions {
        display, enterLine, runFunction, displayMathResult
    }

    private enum DefaultMathOperations {
        add, sub, mul, div, mod
    }
}
