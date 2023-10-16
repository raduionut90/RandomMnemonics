package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CustomThread.class);

    private final Random random = new Random();
    private final int MNEMONIC_SIZE = 12;
    private static final int VOCABULARY_LIMIT = 2048;

    private final List<String> vocabulary;
    private final Web3j web3;


    public CustomThread(List<String> vocabulary, Web3j web3){
        this.vocabulary = vocabulary;
        this.web3 = web3;
    }

    @java.lang.SuppressWarnings("squid:S2189")
    @Override
    public void run() {
        logger.info(Thread.currentThread().getName());

        List<String> mnemonic = new ArrayList<>(MNEMONIC_SIZE);
        int count = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            int randomInt = random.nextInt(VOCABULARY_LIMIT);
            String randomWord = vocabulary.get(randomInt);
            if (!mnemonic.contains(randomWord)) {
                mnemonic.add(randomWord);
            }
            if (mnemonic.size() == MNEMONIC_SIZE) {
                processMnemonic(mnemonic);
                mnemonic.clear();
                count++;
            } else {
                continue;
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            int timeLimit = 1000 * 60 * 10; // 1 sec * 60 sec * 10min

            if (duration >= timeLimit) {
                long rate = count/(duration / 1000);
                logger.info("rate-per-thread: {} all-threads: {}", rate, rate * Config.getInstance().getThreadNumber());
                startTime = endTime;
                count = 0;
            }
        }
    }

    public void processMnemonic(List<String> mnemonicList) {
        String mnemonic = String.join(" ", mnemonicList);
        String address = Utils.getAddressFromMnemonic(mnemonic);
        if (address != null) {
            BigDecimal ethBalance = null;
            try {
                ethBalance = Utils.getBalance(address, web3);
                if (ethBalance.compareTo(BigDecimal.ZERO) != 0) {
                    logger.warn("address: {}  balance: {} mnemonic: {}", address, ethBalance, mnemonic);
                }
            } catch (InterruptedException e) {
                logger.error("InterruptedException: ", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
