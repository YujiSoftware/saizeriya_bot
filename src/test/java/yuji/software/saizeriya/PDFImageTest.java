package yuji.software.saizeriya;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFImageTest {

    private static final List<Integer> PNG_HEADER = List.of(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);

    @Test
    void create() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/ir.pdf")) {
            List<Path> images = PDFImage.create(stream);

            assertEquals(4, images.size());
            for (Path image : images) {
                assertTrue(Files.exists(image));
                assertPNG(image);
            }
        }
    }

    private static void assertPNG(Path path) throws IOException {
        try (InputStream stream = Files.newInputStream(path)) {
            for (int expected : PNG_HEADER) {
                assertEquals(expected, stream.read());
            }
        }
    }
}