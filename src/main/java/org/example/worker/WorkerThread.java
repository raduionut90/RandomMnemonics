package org.example.worker;

import org.example.MnemonicGenerator;
import org.example.MnemonicProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

public class WorkerThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    @Override
    public void run() {
        String mnemonic = MnemonicGenerator.generateMnemonic();
        while (!MnemonicUtils.validateMnemonic(mnemonic)) {
            mnemonic = MnemonicGenerator.generateMnemonic();
        }
        String address = MnemonicProcessor.getAddressFromMnemonic(mnemonic);
        MnemonicProcessor.checkBalance(address, mnemonic);
    }
}
