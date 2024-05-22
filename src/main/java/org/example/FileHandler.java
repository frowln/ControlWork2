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
            int size = dataStream.readInt();

            byte[] dataBytes = new byte[size];
            dataStream.read(dataBytes);

            String content = new String(dataBytes, StandardCharsets.UTF_8);
            int controlSum = dataStream.readInt();
            int partNumber = dataStream.readInt();

            int actualCharCount = content.length();
            int actualByteCount = content.getBytes(StandardCharsets.UTF_8).length;

            logger.log(String.format("Прочтен файл %s, количество байтов: %d, количество символов: %d, сумма: %d, part number: %d",
                    inputFile.getName(), size, actualCharCount, controlSum, partNumber));

            if (size == controlSum && actualByteCount == size) {
                synchronized (fileParts) {
                    fileParts.put(partNumber, content);
                }
            } else {
                logger.log(String.format("Ошибка в файле %s: количество байт не соответствуют фактическим данным", inputFile.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}