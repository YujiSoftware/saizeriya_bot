package yuji.software.saizeriya;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsTest {
    @Test
    public void collect() throws URISyntaxException, IOException {
        String html = Files.readString(Path.of(getClass().getResource("/news.html").toURI()));
        Document doc = Jsoup.parse(html);

        List<News> actual = News.collect(doc);
        assertEquals(27, actual.size());

        News news = actual.get(0);
        assertEquals("12月14日 冬のグランドメニュー改定！", news.title());
        assertEquals("https://www.saizeriya.co.jp/PDF/irpdf001347.pdf", news.url());
    }

}