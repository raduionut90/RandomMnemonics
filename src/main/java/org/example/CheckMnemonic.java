package org.example;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.example.FileProcessorCounter.writeLastProcessedFile;


public class CheckMnemonic {
    private static final Logger logger = LoggerFactory.getLogger(CheckMnemonic.class);
    private static final Config CONFIG = Config.getInstance();
    private static final int VOCABULARY_LIMIT = 2048;
    private static final Web3j web3 = Web3j.build(new HttpService());

    public static void main(String[] args) {
        logger.warn("start");

        List<String> vocabulary = loadVocabulary();
        System.out.println(vocabulary.size());

        for (int i = 0; i < CONFIG.getThreadNumber(); i++) {
            Thread thread = new Thread(new CustomThread(vocabulary, web3));
            thread.start();
        }
    }

    private static List<String> loadVocabulary(){
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
            logger.error("Error: {0}", e);
        }
        return result;
    }
}
