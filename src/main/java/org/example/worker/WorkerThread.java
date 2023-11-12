package org.example.worker;

import org.example.MnemonicProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

public class WorkerThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private final String mnemonic;

    public WorkerThread(String mnemonic){
        this.mnemonic = mnemonic;
    }

    @Override
    public void run() {

        if (MnemonicUtils.validateMnemonic(mnemonic)) {
            String address = MnemonicProcessor.getAddressFromMnemonic(mnemonic);
            MnemonicProcessor.checkBalance(address, mnemonic);
        }
    }


}
