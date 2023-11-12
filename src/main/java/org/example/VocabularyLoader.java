package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VocabularyLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyLoader.class);
    private static final int VOCABULARY_LIMIT = 2048;
    public static final List<String> VOCABULARY = loadVocabulary();

    public static List<String> loadVocabulary(){
        List<String> result = new ArrayList<>(VOCABULARY_LIMIT);
        try (InputStream inputStream = CheckMnemonic.class.getResourceAsStream("/english.txt")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.add(line);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error: {0}", e);
        }
        return result;
    }
}
