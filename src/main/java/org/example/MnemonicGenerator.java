package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MnemonicGenerator {
    private static final Random RANDOM = new Random();
    private static final int MNEMONIC_SIZE = 12;

    public static String generateMnemonic(List<String> vocabulary) {
        List<String> mnemonic = new ArrayList<>(MNEMONIC_SIZE);

        while (mnemonic.size() < MNEMONIC_SIZE) {
            int randomInt = RANDOM.nextInt(vocabulary.size());
            String randomWord = vocabulary.get(randomInt);
            if (!mnemonic.contains(randomWord)) {
                mnemonic.add(randomWord);
            }
        }

        return String.join(" ", mnemonic);
    }
}
