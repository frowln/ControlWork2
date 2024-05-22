package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        try {
            String version = "10";
            String directoryPath = "v" + version;
            Logger logger = new Logger("v" + version + ".log");

            Map<Integer, String> fileParts = new ConcurrentHashMap<>();

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

            try (PrintWriter outputWriter = new PrintWriter(new FileWriter("v" + version + ".txt"))) {
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