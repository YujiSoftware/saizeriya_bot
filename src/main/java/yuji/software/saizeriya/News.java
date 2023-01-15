package yuji.software.saizeriya;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public record News(String title, String url) {
    private static String NEWS_URL = "https://www.saizeriya.co.jp/corporate/news/";

    public static List<News> collect() throws IOException {
        return collect(Jsoup.connect(NEWS_URL).get());
    }

    public static List<News> collect(Document doc) {
        return doc.getElementById("mainCol")
                .getElementsByTag("a")
                .stream()
                .filter(e -> e.attr("href").endsWith(".pdf"))
                .map(e -> new News(e.text(), e.attr("href")))
                .toList();
    }
}
