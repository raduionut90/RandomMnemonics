import org.example.Config;
import org.example.Utils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.example.FileProcessorCounter.updateFilesByLastProcessedFile;
import static org.example.FileProcessorCounter.writeLastProcessedFile;
import static org.junit.Assert.*;

public class TestUtils {

    @ClassRule
    public static final TemporaryFolder TMP_FOLDER = new TemporaryFolder();

    private static final String LAST_PROCESSED_FILE_PATH = "test_last_processed_file.txt";
    private static final String TEST_LAST_PROCESSED_FILE_NAME = "file_3.txt";
    private static Path LAST_PROCESSED_FILE;

    public TestUtils() throws IOException {
        Path x = Paths.get(TMP_FOLDER.getRoot().getAbsolutePath() + "/" + LAST_PROCESSED_FILE_PATH);
        if (!Files.exists(x)){
            LAST_PROCESSED_FILE = TMP_FOLDER.newFile(LAST_PROCESSED_FILE_PATH).toPath();
            Path lastProcessedFile = Paths.get("file_3.txt");
            writeLastProcessedFile(LAST_PROCESSED_FILE, lastProcessedFile);
        }
    }


    @Test
    public void testGetAddressFromMnemonic() {
        String mnemonic1 = "person wagon kingdom sleep mixed glare shy illness mango clarify speed cruel";
        String expectedAddress1 = "0x63F65a55149Cef6B6017520ad74e8cf7702FaF9a";
        String mnemonic2 = "ability people rare sleep harvest jazz cruel pencil glare play flavor supply";
        String expectedAddress2 = "0xfA7ab92859D0f7B35602409c41cecA98C2B59621";

        String actualAddress1 = Utils.getAddressFromMnemonic(mnemonic1);
        String actualAddress2 = Utils.getAddressFromMnemonic(mnemonic2);

        assert actualAddress1 != null;
        assertEquals(expectedAddress1.toLowerCase(), actualAddress1.toLowerCase());
        assert actualAddress2 != null;
        assertEquals(expectedAddress2.toLowerCase(), actualAddress2.toLowerCase());
    }

    @Test
    public void testGetBalance1() throws InterruptedException {
        Web3j web3 = Web3j.build(new HttpService());
        String address = "0x63F65a55149Cef6B6017520ad74e8cf7702FaF9a";
        String expectedBalance = "0.00002372085933";
        BigDecimal actualBalance = Utils.getBalance(address, web3);
        assertEquals(expectedBalance, actualBalance.toString());
        Assert.assertNotEquals(actualBalance.compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void testGetBalance2() throws InterruptedException {
        Web3j web3 = Web3j.build(new HttpService());
        String address = "0x05be40969F80543Af58139105738CD11bF820A5B"; //Uniswap address
        BigDecimal actualBalance = Utils.getBalance(address, web3);
        Assert.assertNotEquals(actualBalance.compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void testWriteLastProcessedFile() throws IOException {
        String actualLastProcessedFileName = Files.readAllLines(LAST_PROCESSED_FILE).get(0);
        assertEquals(TEST_LAST_PROCESSED_FILE_NAME, actualLastProcessedFileName);
    }

    @Test
    public void testUpdateFilesByLastProcessedFile() {
        List<Path> files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            files.add(Paths.get("file_" + i + ".txt"));
        }
        List<Path> actualFiles =  updateFilesByLastProcessedFile(LAST_PROCESSED_FILE, files);

        assertEquals(Paths.get("file_4.txt").toString(), actualFiles.get(0).toString());
        assertEquals(files.size() - 4, actualFiles.size());
    }

    @Test
    public void testPropertiesTxtFiles() {
        Config configInstance = Config.getInstance();
        String inputFiles = configInstance.getInputFilesPath();
        String last_processed_file = configInstance.getLastProcessedFileName();
        int threadNumber = configInstance.getThreadNumber();
        assertNotNull(inputFiles);
        assertNotNull(last_processed_file);
        assertTrue(last_processed_file.endsWith(".txt"));
        assertNotEquals(0, threadNumber);
    }
}
