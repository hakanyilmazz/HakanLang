package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager implements AutoCloseable {
    private final String path;
    private final List<String> textLines;
    private BufferedReader bufferedReader;
    private int lineCount;

    public FileManager(String path) throws IOException {
        this.path = path;
        bufferedReader = createReader(path);
        textLines = readFile(bufferedReader);
        this.lineCount = textLines.size();
    }

    public List<String> search(String... text) {
        List<String> result = new ArrayList<>();
        List<String> editedTexts = new ArrayList<>();

        for (String edit : text) {
            editedTexts.add(edit.trim().toLowerCase());
        }

        String temp = "";
        for (String line : textLines) {
            temp = line.trim().toLowerCase();
            for (String edit : editedTexts) {
                if (temp.contains(edit)) {
                    result.add(line);
                }
            }
        }

        return result;
    }

    private List<String> readFile(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line.trim());
        }

        return lines;
    }

    private BufferedReader createReader(String path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path));
    }

    public String getPath() {
        return path;
    }

    public List<String> getTextLines() {
        return textLines;
    }

    @Override
    public void close() throws Exception {
        bufferedReader.close();
    }

    public int getLineCount() {
        return lineCount;
    }
}
