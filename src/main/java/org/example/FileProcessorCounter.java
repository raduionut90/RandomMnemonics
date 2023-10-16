package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


public class FileProcessorCounter {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorCounter.class);

    private FileProcessorCounter() {
    }

    public static void writeLastProcessedFile(Path lastProcessedFilePath, Path file) {
        // Salvare numele ultimului fișier procesat cu succes
        try {
            String filename = file.toString();
            String lastProcessedFileName = getLastProcessedFileName(lastProcessedFilePath);

            if (!lastProcessedFileName.isEmpty()) {
                int number1 = Integer.parseInt(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf(".")));
                int number2 = Integer.parseInt(lastProcessedFileName.substring(lastProcessedFileName.lastIndexOf("_") + 1, lastProcessedFileName.lastIndexOf(".")));
                if (number1 > number2)
                    Files.write(lastProcessedFilePath, Collections.singletonList(file.toString()));
            } else {
                Files.write(lastProcessedFilePath, Collections.singletonList(file.toString()));
            }


        } catch (IOException e) {
            logger.error("Eroare la salvarea ultimului fișier procesat: {}", e.getMessage());
        }
    }

    public static List<Path> updateFilesByLastProcessedFile(Path lastProcessedFilePath, List<Path> files) {
        String lastProcessedFileName;
        lastProcessedFileName = getLastProcessedFileName(lastProcessedFilePath);

        // Găsiți indexul fișierului corespunzător în lista de fișiere
        int lastIndex = files.indexOf(Paths.get(lastProcessedFileName));

        // Dacă fișierul corespunzător nu a fost găsit, începeți procesarea de la început
        if (lastIndex == -1) {
            logger.info("Nu s-a găsit fișierul corespunzător ultimului fișier procesat. Începeți de la început.");
        } else {
            logger.info("Starting with file {}", lastIndex);
            lastIndex++; // Începeți de la următorul fișier după ultimul procesat cu succes
            files = files.subList(lastIndex, files.size());
        }
     
        return files;
    }

    private static String getLastProcessedFileName(Path lastProcessedFilePath) {
        String lastProcessedFileName;
        try {
            if (Files.exists(lastProcessedFilePath) && Files.isRegularFile(lastProcessedFilePath)
                    && !Files.readAllLines(lastProcessedFilePath).isEmpty()) {
                lastProcessedFileName = Files.readAllLines(lastProcessedFilePath).get(0);
            } else {
                lastProcessedFileName = "";
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            lastProcessedFileName = "";
            logger.error("Eroare la citirea fișierului ultimului fișier procesat: {}", e.getMessage());
        }
        return lastProcessedFileName;
    }

}
