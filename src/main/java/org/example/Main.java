package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            String directoryPath = "v10";
            Logger logger = new Logger("v10.log");

            Map<Integer, String> fileParts = new HashMap<>();

            File directory = new File(directoryPath);
            File[] fileList = directory.listFiles();

            if (fileList != null) {
                List<Thread> threadList = new ArrayList<>();

                for (File file : fileList) {
                    Thread thread = new Thread(new FileHandler(file, logger, fileParts));
                    threadList.add(thread);
                    thread.start();
                }

                for (Thread thread : threadList) {
                    thread.join();
                }
            }

            List<Integer> partNumbers = new ArrayList<>(fileParts.keySet());
            Collections.sort(partNumbers);

            try (PrintWriter outputWriter = new PrintWriter(new FileWriter("v10.txt"))) {
                for (int partNumber : partNumbers) {
                    outputWriter.print(fileParts.get(partNumber));
                }
            }

            logger.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}