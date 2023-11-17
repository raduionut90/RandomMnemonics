package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class MnemonicProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MnemonicProcessor.class);
    private static final Web3j WEB3 = Web3j.build(new HttpService());

    private MnemonicProcessor() {
    }

    public static void checkBalance(String address, String mnemonic) {
        BigInteger ethBalance = getBalance(address);
        if (!ethBalance.equals(BigInteger.ZERO)) {
            LOGGER.warn("$$==========================================$$");
            LOGGER.warn("address: {}  balance: {} mnemonic: {}", address, ethBalance, mnemonic);
            LOGGER.warn("$$==========================================$$");
        } else {
            LOGGER.debug("{}, balance:{}", mnemonic, ethBalance);
        }
    }

    public static String getAddressFromMnemonic(String mnemonic) {
        int[] path = new int[]{-2147483604, -2147483588, Integer.MIN_VALUE, 0, 0};

        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");
//        byte[] seed1 = PBKDF2SHA512.derive(mnemonic, "mnemonic", 2048, 64);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
        return Numeric.prependHexPrefix(Keys.getAddress(bip44Keypair));
    }
    public static BigInteger getBalance(String address) {
        try  {
            EthGetBalance balanceResponse = WEB3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return balanceResponse.getBalance();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
