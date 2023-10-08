package org.example;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.FileProcessorCounter.updateFilesByLastProcessedFile;
import static org.example.FileProcessorCounter.writeLastProcessedFile;


public class CheckMnemonic {
    private static final Logger logger = LoggerFactory.getLogger(CheckMnemonic.class);
    protected static final Properties appProps = new Properties();
    private static final int THREAD_NUMBER = 6;
    private static final Object fileWriteLock = new Object();
    private static final JsonMapper objectMapper = new JsonMapper();

    public static void main(String[] args) {
        logger.warn("start");
        loadProperties();
        String txtFilesPath = appProps.getProperty("txt_files_path");
        String lastProcessedFilePath = appProps.getProperty("last_processed_file");
        Path txtFiles = Paths.get(txtFilesPath);
        List<Path> files = Utils.getTxtFiles(txtFiles);
        Path lastProcessedFile = Paths.get(lastProcessedFilePath);
        files = updateFilesByLastProcessedFile(lastProcessedFile, files);

        Web3j web3 = Web3j.build(new HttpService());

        try (ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER)) {
            List<Callable<Void>> callables = getCallables(files, web3, lastProcessedFile);
            executorService.invokeAll(callables);

        } catch (Exception e) {
            logger.error("Exception: {0}", e);
            Thread.currentThread().interrupt();
        }
        logger.warn("end");
    }

    private static void loadProperties() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";
        try (var reader = Files.newBufferedReader(Paths.get(appConfigPath))) {
            appProps.load(reader);
        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage());
        }
    }

    @NotNull
    private static List<Callable<Void>> getCallables(List<Path> files, Web3j web3, Path lastProcessedFilePath) {

        List<Callable<Void>> callables = new ArrayList<>();
        for (Path file : files) {
            callables.add(() -> {
                processFile(file, web3);
                synchronized (fileWriteLock){
                    writeLastProcessedFile(lastProcessedFilePath, file);
                }
                return null;
            });
        }
        return callables;
    }

    private static void processFile(Path file, Web3j web3) {
        long startTime = System.currentTimeMillis();

        try {
            List<String> fileContent = Files.readAllLines(file);
//            Map<String, String> results = new HashMap<>();

            for (String mnemonic: fileContent) {
                String address = Utils.getAddressFromMnemonic(mnemonic);
                if (address != null) {
//                    results.put(address, mnemonic);
                    BigDecimal ethBalance = Utils.getBalance(address, web3);
                    if (ethBalance.compareTo(BigDecimal.ZERO) != 0) {
                        logger.warn("address: {}  balance: {} mnemonic: {}", address, ethBalance, mnemonic);
                    }
                }
            }
//            writeResults(results, file.getFileName().toString());
        } catch (IOException e) {
            logger.error("IOException exception: ", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurred while fetching balance: {0}", e);
            Thread.currentThread().interrupt(); // Re-interrupt current thread
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        long executionTimeSec = executionTime / 1000;
        long ratePerSec = 1000000 / executionTimeSec * THREAD_NUMBER;
        logger.debug("{} {} ms, {} sec, rate {}/s",file.getFileName(), executionTime, executionTimeSec, ratePerSec);
    }

    public static void writeResults(Map<String, String> results, String filename) throws IOException {
        String resultFileName = appProps.getProperty("result_file") + filename;
        Path resultFile = Paths.get(resultFileName);

        if (!Files.exists(resultFile)) {
            Files.createFile(resultFile);
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(resultFile.toFile(), results);
    }



}
