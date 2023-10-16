package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getAddressFromMnemonic(String mnemonic) {
        if (MnemonicUtils.validateMnemonic(mnemonic)) {
            int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, HARDENED_BIT, 0,0};

            byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
            return Numeric.prependHexPrefix(Keys.getAddress(bip44Keypair));

        }
        return null;
    }

    public static BigDecimal getBalance(String address, Web3j web3) throws InterruptedException {
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = web3
                    .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
        } catch (ExecutionException e) {
            logger.error("ExecutionException occurred while fetching balance: {0}", e);
            Thread.sleep((60000));
            getBalance(address, web3);
        }
        BigInteger wei = ethGetBalance == null ? BigInteger.ZERO : ethGetBalance.getBalance();

        return Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
    }


}
