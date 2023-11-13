package org.example;

import java.util.*;

import static org.web3j.crypto.Hash.sha256;

public class MnemonicGenerator {
    private static final Random RANDOM = new Random();
    private static final int MNEMONIC_SIZE = 12;

    private static final List<String> vocabulary = VocabularyLoader.loadVocabulary();

    public static String generateMnemonic() {
        List<String> mnemonicList = new ArrayList<>(MNEMONIC_SIZE);

        while (mnemonicList.size() < MNEMONIC_SIZE) {
            int randomInt = RANDOM.nextInt(vocabulary.size());
            String randomWord = vocabulary.get(randomInt);
            if (!mnemonicList.contains(randomWord)) {
                mnemonicList.add(randomWord);
            }
        }


        String mnemonic = String.join(" ", mnemonicList);
        return isValidMnemonic(mnemonic) ? mnemonic : generateMnemonic();
    }

    public static boolean isValidMnemonic(String mnemonic) {
        final BitSet bits = new BitSet();
        final int size = mnemonicToBits(mnemonic, bits);
        final byte[] entropy = new byte[128 / 8];
        for (int i = 0; i < entropy.length; i++) {
            entropy[i] = readByte(bits, i);
        }
//        validateEntropy(entropy);

        final byte expectedChecksum = calculateChecksum(entropy);
        final byte actualChecksum = readByte(bits, entropy.length);
        if (expectedChecksum != actualChecksum) {
            return false;
        }

        return true;
    }

    private static int mnemonicToBits(String mnemonic, BitSet bits) {
        int bit = 0;
        final StringTokenizer tokenizer = new StringTokenizer(mnemonic, " ");
        while (tokenizer.hasMoreTokens()) {
            final String word = tokenizer.nextToken();
            final int index = vocabulary.indexOf(word);
            if (index < 0) {
                throw new IllegalArgumentException(
                        String.format("Mnemonic word '%s' should be in the word list", word));
            }
            for (int k = 0; k < 11; k++) {
                bits.set(bit++, isBitSet(index, 10 - k));
            }
        }
        return bit;
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
