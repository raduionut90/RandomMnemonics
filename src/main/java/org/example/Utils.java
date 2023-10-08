package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    public static List<Path> getTxtFiles(Path dir) {
        List<Path> files = new ArrayList<>();
        if (Files.isDirectory(dir) && Files.exists(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                stream.forEach(files::add);
            } catch (IOException e) {
                logger.error("IOException occurred while fetching files: {0}", e);
            }

            files = files.stream().filter(f -> f.getFileName().toString().endsWith(".txt")).collect(Collectors.toList());
            // Sortarea fișierelor în funcție de nume
            files.sort(Comparator.comparing(Path::getFileName, (p1, p2) -> {
                String name1 = p1.toString();
                String name2 = p2.toString();

                // Extrage numărul din numele fișierului
                int number1 = Integer.parseInt(name1.substring(name1.lastIndexOf("_") + 1, name1.lastIndexOf(".")));
                int number2 = Integer.parseInt(name2.substring(name2.lastIndexOf("_") + 1, name2.lastIndexOf(".")));

                return Integer.compare(number1, number2);
            }));
        } else {
            logger.error("Empty dir: {}", dir.getFileName());
        }
        return files;
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
