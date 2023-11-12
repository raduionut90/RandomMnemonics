package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties appProps = new Properties();

    private static final AppConfig INSTANCE = new AppConfig();

    private int threadNumber;
    private int rateMinutes;

    private AppConfig(){
        try (InputStream inputStream = CheckMnemonic.class.getResourceAsStream("/app.properties")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    appProps.load(reader);
                    threadNumber = Integer.parseInt(appProps.getProperty("thread_number"));
                    rateMinutes = Integer.parseInt(appProps.getProperty("rate_minutes"));
                }
            }
        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage());
        }
    }

    public static synchronized AppConfig getInstance() {
        return INSTANCE;
    }

    public int getThreadNumber() {
        return threadNumber;
    }
    public int getRateMinutes() { return rateMinutes; }
}
