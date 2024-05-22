package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileHandler implements Runnable {
    private final File inputFile;
    private final Logger logger;
    private final Map<Integer, String> fileParts;

    public FileHandler(File inputFile, Logger logger, Map<Integer, String> fileParts) {
        this.inputFile = inputFile;
        this.logger = logger;
        this.fileParts = fileParts;
    }

    @Override
    public void run() {
        try (DataInputStream dataStream = new DataInputStream(new FileInputStream(inputFile))) {
            int expectedSize = dataStream.readInt();
            int remainingBytes = (int) (inputFile.length() - Integer.BYTES * 3);

            byte[] dataBytes = new byte[remainingBytes];
            dataStream.read(dataBytes);

            String content = new String(dataBytes, StandardCharsets.UTF_8);
            int controlSum = dataStream.readInt();
            int partNumber = dataStream.readInt();

            int actualCharCount = content.length();
            int actualByteCount = content.getBytes(StandardCharsets.UTF_8).length;

            logger.log(String.format("Read file %s, expected byte count: %d, actual byte count: %d, character count: %d, control sum: %d, part number: %d",
                    inputFile.getName(), expectedSize, actualByteCount, actualCharCount, controlSum, partNumber));

            if (actualCharCount == controlSum && actualByteCount == expectedSize) {
                synchronized (fileParts) {
                    fileParts.put(partNumber, content);
                }
            } else {
                logger.log(String.format("Error in file %s: control sum or byte count do not match actual data", inputFile.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}