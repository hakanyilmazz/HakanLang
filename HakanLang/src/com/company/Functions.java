package com.company;

import java.util.HashMap;
import java.util.Map;

public class Functions {
    private static final Map<String, Map<String, Object>> functions = new HashMap<>();

    public void createFunction(String line) {
        if (!line.startsWith("f")) {
            return;
        }

        line = line.trim();
        String functionName = line.substring(line.indexOf("-") + 1, line.indexOf("("));
        String[] parameters = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim().split(",");

        String codeLine = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}")).trim();
        final Map<String, Object> functionDetail = new HashMap<>();

        if (!codeLine.contains("#return ")) {
            functionDetail.put("parameters", parameters);
        } else {
            String returnValue = codeLine.substring(line.indexOf("#return ") + 8);
            returnValue = "#~" + functionName + " = " + returnValue;
            codeLine = codeLine.substring(0, line.indexOf("#return "));
            codeLine += returnValue;
        }

        functionDetail.put("codes", codeLine.trim());

        functions.put(functionName, functionDetail);
    }

    public String runFunction(String line) {
        if (!line.startsWith("~")) {
            return "";
        }

        line = line.trim();

        String functionName = line.substring(line.indexOf("~") + 1, line.indexOf("("));

        if (!functions.containsKey(functionName)) {
            return "";
        }

        final Map<String, Object> functionValueWithParameters = functions.get(functionName);
        return (String) functionValueWithParameters.get("codes");
    }
}
