package org.example.worker;

import org.example.MnemonicGenerator;
import org.example.MnemonicProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

import java.util.List;

public class WorkerThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private final List<String> vocabulary;

    public WorkerThread(List<String> vocabulary){
        this.vocabulary = vocabulary;
    }

    @Override
    public void run() {
        String mnemonic = MnemonicGenerator.generateMnemonic(vocabulary);
        while (!MnemonicUtils.validateMnemonic(mnemonic)) {
            mnemonic = MnemonicGenerator.generateMnemonic(vocabulary);
        }
        String address = MnemonicProcessor.getAddressFromMnemonic(mnemonic);
        MnemonicProcessor.checkBalance(address, mnemonic);
    }
}
