import com.sbytestream.CmdLineParser;
import com.sbytestream.Splitter;
import com.sbytestream.ValidationResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SplitterTests {
    @Test
    public void given_no_mode_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-test"});
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("mode has not been specified. Use either -s or -j.", r.getMessage());
    }

    @Test
    public void given_split_with_no_size_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-i", "sample.zip" });
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Size has not been specified. Use the -n option.", r.getMessage());
    }

    @Test
    public void given_split_with_non_numeric_size_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-n", "foo" });
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Invalid size has been specified.", r.getMessage());
    }

    @Test
    public void given_split_with_less_than_min_size_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-n", "3000" });
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Size must be equal or greater than 4096 bytes.", r.getMessage());

    }

    @Test
    public void given_split_with_non_multiple_of_min_size_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-n", "7000" });
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Size must multiples of 4096 bytes.", r.getMessage());
    }

    @Test
    public void given_no_input_file_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-n", "8192" });
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Input file not specified.", r.getMessage());
    }

    @Test
    public void given_input_file_with_no_value_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-n", "4096", "-i"});
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Input file not specified.", r.getMessage());
    }

    @Test
    public void given_no_output_file_returns_false() {
        CmdLineParser cmd = new CmdLineParser(new String[] { "-s", "-i", "sample.zip", "-n", "4096", "-o" });
        ValidationResult r = new Splitter().validate(cmd);
        assertEquals(false, r.isResult());
        assertEquals("Output file not specified.", r.getMessage());
    }

    @Test
    public void give_a_single_digit_chunk_returns_filename() {
        assertEquals("sample.001", Splitter.getFileName("sample", 1));
    }

    @Test
    public void give_a_double_digit_chunk_returns_filename() {
        assertEquals("sample.021", Splitter.getFileName("sample", 21));
    }

    @Test
    public void give_a_triple_digit_chunk_returns_filename() {
        assertEquals("sample.321", Splitter.getFileName("sample", 321));
    }
}
