import com.sbytestream.CmdLineParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CmdLineParserTests {
    @Test
    public void given_param_returns_corresponding_value() {
        CmdLineParser c = new CmdLineParser(new String[] { "-host", "localhost" } );
        assertEquals("localhost",  c.getParamValue("host"));
    }

    @Test
    public void given_two_params_returns_corresponding_values() {
        CmdLineParser c = new CmdLineParser(new String[] { "-host", "localhost", "-port", "8080" } );
        assertEquals("localhost",  c.getParamValue("host"));
        assertEquals("8080",  c.getParamValue("port"));
    }

    @Test
    public void given_a_flags_we_can_detect_it() {
        CmdLineParser c = new CmdLineParser(new String[] { "-silent" } );
        assertEquals(true, c.hasFlag("silent"));
    }

    @Test
    public void given_param_with_value_does_not_detect_as_flag() {
        CmdLineParser c = new CmdLineParser(new String[] { "-host", "localhost" } );
        assertFalse(c.hasFlag("host"));
    }

    @Test
    public void given_two_flags_we_can_detect_them() {
        CmdLineParser c = new CmdLineParser(new String[] { "-silent", "-loud" } );
        assertEquals(true, c.hasFlag("silent"));
        assertEquals(true, c.hasFlag("loud"));
    }

    @Test
    void given_a_non_existent_flag_is_detected_as_not_present() {
        CmdLineParser c = new CmdLineParser(new String[] { "-silent", "-loud" } );
        assertFalse(c.hasFlag("shouldNotExists"));
    }

    @Test
    void given_a_non_existent_flag_and_args_is_empty_is_detected_as_not_present() {
        CmdLineParser c = new CmdLineParser(new String[] { } );
        assertFalse(c.hasFlag("shouldNotExists"));
    }

    @Test
    public void given_a_flag_and_a_param_with_value_get_currect_results() {
        CmdLineParser c = new CmdLineParser(new String[] { "-port", "8080", "-silent" } );
        assertEquals("8080", c.getParamValue("port"));
        assertTrue(c.hasFlag("silent"));
        assertFalse(c.hasFlag("notpresent"));
    }

    @Test
    public void given_a_raw_value_access_by_index_zero_is_successful() {
        CmdLineParser c = new CmdLineParser(new String[] { "localhost" });
        assertEquals("localhost", c.getAt(0));
    }

    @Test
    public void given_two_raw_values_access_by_index_is_successful() {
        CmdLineParser c = new CmdLineParser(new String[] { "localhost", "8080" });
        assertEquals("localhost", c.getAt(0));
        assertEquals("8080", c.getAt(1));
    }

    @Test
    public void given_everything_it_works() {
        CmdLineParser c = new CmdLineParser(new String[] { "localhost", "-port", "8080", "-silent", "-user", "admin", "-noecho" });
        assertEquals("localhost", c.getAt(0));
        assertEquals("8080", c.getParamValue("port"));
        assertTrue(c.hasFlag("silent"));
        assertEquals("admin", c.getParamValue("user"));
        assertTrue(c.hasFlag("noecho"));
    }
}
