import org.example.AppConfig;
import org.example.MnemonicGenerator;
import org.example.MnemonicProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.MnemonicUtils;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.*;

public class TestUtils {

    @Test
    public void testGetAddressFromMnemonic() {
        String mnemonic1 = "person wagon kingdom sleep mixed glare shy illness mango clarify speed cruel";
//        String mnemonic1 = "person wagon kingdom sleep mixed glare shy illness mango clarify speed";
        String expectedAddress1 = "0x63F65a55149Cef6B6017520ad74e8cf7702FaF9a";
        String mnemonic2 = "ability people rare sleep harvest jazz cruel pencil glare play flavor supply";
        String expectedAddress2 = "0xfA7ab92859D0f7B35602409c41cecA98C2B59621";

        String actualAddress1 = MnemonicProcessor.getAddressFromMnemonic(mnemonic1);
        String actualAddress2 = MnemonicProcessor.getAddressFromMnemonic(mnemonic2);

        assert actualAddress1 != null;
        assertEquals(expectedAddress1.toLowerCase(), actualAddress1.toLowerCase());
        assert actualAddress2 != null;
        assertEquals(expectedAddress2.toLowerCase(), actualAddress2.toLowerCase());
    }

    @Test
    public void testIsValidMnemonic() {
        for (int i = 0; i < 10000; i++) {
            List<String> mnemonic = MnemonicGenerator.generateMnemonic();
            boolean mnemonicUtilsIsValid = MnemonicUtils.validateMnemonic(String.join(" ", mnemonic));
            assertTrue(mnemonicUtilsIsValid);
        }
    }

    @Test
    public void testGetBalance() {
        String mnemonic = "priority apology asthma comic athlete ring setup bacon congress sibling sting swallow";
        String expectedAddress = "0x1999dd3f93e746f1fea406574f653806a4aa4341";
        String actualAddress = MnemonicProcessor.getAddressFromMnemonic(mnemonic);
        BigInteger actualBalance = MnemonicProcessor.getBalance(actualAddress);

        MnemonicProcessor.checkBalance(actualAddress, mnemonic);
        assertFalse(MnemonicUtils.validateMnemonic(mnemonic));
        assertEquals(expectedAddress.toLowerCase(), actualAddress.toLowerCase());
        assertEquals(actualBalance.compareTo(BigInteger.ZERO), 0);
    }

    @Test
    public void testGetBalance1()  {
        String address = "0x63F65a55149Cef6B6017520ad74e8cf7702FaF9a";
        BigInteger actualBalance = MnemonicProcessor.getBalance(address);
        assertNotEquals(actualBalance.compareTo(BigInteger.ZERO), 0);
    }

    @Test
    public void testGetBalance2() {
        String address = "0x05be40969F80543Af58139105738CD11bF820A5B"; //Uniswap address
        BigInteger actualBalance = MnemonicProcessor.getBalance(address);
        Assert.assertNotEquals(actualBalance.compareTo(BigInteger.ZERO), 0);
    }

    @Test
    public void testPropertiesTxtFiles() {
        AppConfig appConfigInstance = AppConfig.getInstance();
        int threadNumber = appConfigInstance.getThreadNumber();
        int rateMinutes = appConfigInstance.getRateMinutes();
        assertNotEquals(0, threadNumber);
        assertNotEquals(0, rateMinutes);
    }
}
