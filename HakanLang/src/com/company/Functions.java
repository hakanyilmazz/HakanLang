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

        String returnValue = "";
        if (codeLine.contains("##return ")) {
            returnValue = codeLine.substring(codeLine.indexOf("##return ") + 8).trim();
            codeLine = codeLine.substring(0, codeLine.indexOf("##return "));
        }

        functionDetail.put("parameters", parameters);
        functionDetail.put("codes", codeLine.trim());
        functionDetail.put(functionName, returnValue);

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

        if (functionValueWithParameters.get(functionName).equals("")) {
            return (String) functionValueWithParameters.get("codes");
        } else {
            return functionValueWithParameters.get("codes") + "\n" + getReturnValue(functionName);
        }
    }

    public String getReturnValue(String functionName) {
        return ("##" + functions.get(functionName).get(functionName)).replaceAll("\"", "");
    }
}
