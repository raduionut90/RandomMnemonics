package org.example.utils;

import org.bouncycastle.crypto.digests.SHA512Digest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MnemonicUtils {
    private static final int SEED_ITERATIONS = 2048;
    public static byte[] generateSeed(String mnemonic, String passphrase) {
        passphrase = passphrase == null ? "" : passphrase;

        String salt = String.format("mnemonic%s", passphrase);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest(), mnemonic.getBytes(UTF_8), salt.getBytes(UTF_8), SEED_ITERATIONS);

        return gen.generateDerivedKey(64);
    }

}
