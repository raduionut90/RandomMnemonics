package org.example.worker;

import org.example.MnemonicProcessor;

public class WorkerThread implements Runnable {
    private final String mnemonic;

    public WorkerThread(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @Override
    public void run() {
        String address = MnemonicProcessor.getAddressFromMnemonic(mnemonic);
        MnemonicProcessor.checkBalance(address, mnemonic);
    }
}
