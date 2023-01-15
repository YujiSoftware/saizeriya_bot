package yuji.software.saizeriya;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class PDFImage {

    public static List<Path> create(URL url) throws IOException {
        try (InputStream stream = url.openStream()) {
            return create(stream);
        }
    }

    public static List<Path> create(InputStream stream) throws IOException {
        Path temp = Files.createTempDirectory("saizeriya");

        try (PDDocument document = PDDocument.load(stream)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pages = document.getNumberOfPages();
            Path[] paths = new Path[pages];

            for (int i = 0; i < pages; i++) {
                BufferedImage bufferedImage = renderer.renderImage(i, 2);

                paths[i] = temp.resolve(Path.of(i + ".png"));
                ImageIO.write(bufferedImage, "png", paths[i].toFile());
            }

            return Arrays.asList(paths);
        }
    }
}
