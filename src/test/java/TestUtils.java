import org.example.AppConfig;
import org.example.MnemonicProcessor;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestUtils {

    @Test
    public void testGetAddressFromMnemonic() {
        String mnemonic1 = "person wagon kingdom sleep mixed glare shy illness mango clarify speed cruel";
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
    public void testGetBalance1()  {
        String address = "0x63F65a55149Cef6B6017520ad74e8cf7702FaF9a";
        String expectedBalance = "0x1592f164e5d0";
        String actualBalance = MnemonicProcessor.getBalance(address);
        assertEquals(expectedBalance, actualBalance.toString());
        Assert.assertNotEquals(actualBalance.compareTo(""), 0);
    }

    @Test
    public void testGetBalance2() {
        String address = "0x05be40969F80543Af58139105738CD11bF820A5B"; //Uniswap address
        String actualBalance = MnemonicProcessor.getBalance(address);
        Assert.assertNotEquals(actualBalance.compareTo("BigInteger.ZERO"), 0);
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
