import com.sbytestream.AppException;
import com.sbytestream.ManifestData;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ManifestDataTests {
    @Test
    public void given_valid_data_returns_object() throws AppException {
        List<String> lines = new ArrayList<>();
        lines.add("chunks: 19");
        lines.add("filename: mockui-A.png");
        ManifestData data = ManifestData.parse(lines);
        assertEquals(19, data.getChunks().intValue());
        assertEquals("mockui-A.png", data.getFilename());
    }

    @Test
    public void given_invalid_data_throw_exception()  {
        List<String> lines = new ArrayList<>();
        lines.add("chunks= 19");
        assertThrows(AppException.class, () -> { ManifestData data = ManifestData.parse(lines);});
    }

    @Test
    public void given_empty_data_returns_empty_manifest() throws AppException {
        List<String> lines = new ArrayList<>();
        ManifestData data = ManifestData.parse(lines);
        assertEquals(null, data.getFilename());
        assertEquals(null, data.getChunks());

        data = ManifestData.parse(null);
        assertEquals(null, data.getFilename());
        assertEquals(null, data.getChunks());
    }

    @Test
    public void given_manifest_data_returns_string() {
        ManifestData data = new ManifestData();
        data.setFilename("foo");
        data.setChunks(10);
        String expected = "chunks: 10" + System.getProperty("line.separator") + "filename: foo";
        assertEquals(expected, data.toString());
    }
}
