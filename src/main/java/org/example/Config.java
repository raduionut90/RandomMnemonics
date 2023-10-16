package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final Properties appProps = new Properties();

    private static final Config INSTANCE = new Config();

    private int threadNumber;
    private String inputFilesPath;
    private String lastProcessedFileName;

    private Config(){
        try (InputStream inputStream = CheckMnemonic.class.getResourceAsStream("/app.properties")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    appProps.load(reader);
                    inputFilesPath = appProps.getProperty("input_files");
                    lastProcessedFileName = appProps.getProperty("last_processed_file");
                    threadNumber = Integer.parseInt(appProps.getProperty("thread_number"));
                }
            }
        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage());
        }
    }

    public static synchronized Config getInstance() {
        return INSTANCE;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public String getInputFilesPath() {
        return inputFilesPath;
    }

    public String getLastProcessedFileName() {
        return lastProcessedFileName;
    }
}
