import com.sbytestream.PathUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PathUtilTests {
    @Test
    public void given_path_with_a_filename_returns_filename() {
        String input = PathUtils.combinePaths("c:", "temp", "foo.txt");
        assertEquals("foo.txt", PathUtils.getFilename(input));
    }

    @Test
    public void given_a_filename_returns_filename() {
        assertEquals("foo.txt", PathUtils.getFilename("foo.txt"));
    }

    @Test
    public void given_path_with_a_filename_returns_filename_without_extension() {
        String input = PathUtils.combinePaths("c:", "temp", "foo.txt");
        assertEquals("foo", PathUtils.getFilenameWithoutExtention(input));
    }

    @Test
    public void given_path_with_a_filename_without_extension_returns_filename_without_extension() {
        String input = PathUtils.combinePaths("c:", "temp", "foo");
        assertEquals("foo", PathUtils.getFilenameWithoutExtention(input));
    }

    @Test
    public void given_a_filename_with_extension_returns_filename_without_extension() {
        assertEquals("foo", PathUtils.getFilenameWithoutExtention("foo.txt"));
    }

    @Test
    public void given_a_filename_without_extension_returns_filename() {
        assertEquals("foo", PathUtils.getFilenameWithoutExtention("foo"));
    }

    @Test
    public void given_two_parts_returns_path() {
        String expected = "foo" + File.separator + "bar";
        assertEquals(expected, PathUtils.combinePaths("foo", "bar"));
    }
}