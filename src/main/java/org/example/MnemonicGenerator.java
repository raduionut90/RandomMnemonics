package org.example;

import java.util.*;

import static org.web3j.crypto.Hash.sha256;

public class MnemonicGenerator {
    private static final Random RANDOM = new Random();
    private static final int MNEMONIC_SIZE = 12;

    private static final List<String> vocabulary = VocabularyLoader.loadVocabulary();

    public static List<String> generateMnemonic() {
        TreeMap<String, Integer> mnemonicList = new TreeMap<>();

        processMnemonicList(mnemonicList);
        while (!isValidMnemonic(mnemonicList)) {
            mnemonicList.pollFirstEntry();
            processMnemonicList(mnemonicList);
        }

        return mnemonicList.keySet().stream().toList();
    }

    private static void processMnemonicList(Map<String, Integer> mnemonicList) {
        while (mnemonicList.size() < MNEMONIC_SIZE) {
            int randomInt = RANDOM.nextInt(vocabulary.size());
            String randomWord = vocabulary.get(randomInt);
            if (!mnemonicList.containsKey(randomWord)) {
                mnemonicList.put(randomWord, randomInt);
            }
        }
    }

    public static boolean isValidMnemonic(Map<String, Integer> mnemonic) {
        final BitSet bits = new BitSet();
        mnemonicToBits(mnemonic, bits);
        final byte[] entropy = new byte[128 / 8];
        for (int i = 0; i < entropy.length; i++) {
            entropy[i] = readByte(bits, i);
        }

        final byte expectedChecksum = calculateChecksum(entropy);
        final byte actualChecksum = readByte(bits, entropy.length);
        return expectedChecksum == actualChecksum;
    }

    private static void mnemonicToBits(Map<String, Integer> mnemonic, BitSet bits) {
        int bit = 0;

        for (Map.Entry<String, Integer> word : mnemonic.entrySet()) {
            final int index = word.getValue();
            for (int k = 0; k < 11; k++) {
                bits.set(bit++, isBitSet(index, 10 - k));
            }
        }
    }

    private static boolean isBitSet(int n, int k) {
        return ((n >> k) & 1) == 1;
    }

    private static byte readByte(BitSet bits, int startByte) {
        byte res = 0;
        for (int k = 0; k < 8; k++) {
            if (bits.get(startByte * 8 + k)) {
                res = (byte) (res | (1 << (7 - k)));
            }
        }
        return res;
    }

    public static byte calculateChecksum(byte[] initialEntropy) {
        int ent = initialEntropy.length * 8;
        byte mask = (byte) (0xff << 8 - ent / 32);
        byte[] bytes = sha256(initialEntropy);

        return (byte) (bytes[0] & mask);
    }
}
