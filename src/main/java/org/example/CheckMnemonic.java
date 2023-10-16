package org.example;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.FileProcessorCounter.updateFilesByLastProcessedFile;
import static org.example.FileProcessorCounter.writeLastProcessedFile;


public class CheckMnemonic {
    private static final Logger logger = LoggerFactory.getLogger(CheckMnemonic.class);
    private static final Object fileWriteLock = new Object();
    private static final Config CONFIG = Config.getInstance();

    public static void main(String[] args) {
        logger.warn("start");

        Path txtFiles = Paths.get(CONFIG.getInputFilesPath());
        String lastProcessedFilePath = CONFIG.getLastProcessedFileName();
        List<Path> files = Utils.getTxtFiles(txtFiles);
        Path lastProcessedFile = Paths.get(lastProcessedFilePath);
        files = updateFilesByLastProcessedFile(lastProcessedFile, files);

        Web3j web3 = Web3j.build(new HttpService());

        try (ExecutorService executorService = Executors.newFixedThreadPool(CONFIG.getThreadNumber())) {
            List<Callable<Void>> callables = getCallables(files, web3, lastProcessedFile);
            executorService.invokeAll(callables);

        } catch (Exception e) {
            logger.error("Exception: {0}", e);
            Thread.currentThread().interrupt();
        }
        logger.warn("end");
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
            for (String mnemonic: fileContent) {
                String address = Utils.getAddressFromMnemonic(mnemonic);
                if (address != null) {
                    BigDecimal ethBalance = Utils.getBalance(address, web3);
                    if (ethBalance.compareTo(BigDecimal.ZERO) != 0) {
                        logger.warn("address: {}  balance: {} mnemonic: {}", address, ethBalance, mnemonic);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOException exception: ", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurred while fetching balance: {0}", e);
            Thread.currentThread().interrupt(); // Re-interrupt current thread
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        long executionTimeSec = executionTime / 1000;
        long ratePerSec = 1000000 / executionTimeSec * CONFIG.getThreadNumber();
        logger.debug("{} {} ms, {} sec, rate {}/s",file.getFileName(), executionTime, executionTimeSec, ratePerSec);
    }
}
