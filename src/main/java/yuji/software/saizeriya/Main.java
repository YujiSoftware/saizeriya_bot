package yuji.software.saizeriya;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.TweetsResources;
import twitter4j.v1.UploadedMedia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    private static final Path CACHE_FILE = Path.of("saizeriya.txt");

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        logger.info("Start {}", Main.class);

        TwitterToken token = TwitterToken.load();

        Set<String> used = new HashSet<>();
        if (Files.exists(CACHE_FILE)) {
            used.addAll(Files.readAllLines(CACHE_FILE));
        }

        try (BufferedWriter writer = Files.newBufferedWriter(CACHE_FILE, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            List<News> newsList = News.collect();
            for (News news : newsList) {
                if (used.contains(news.url())) {
                    continue;
                }

                logger.info("Found news [title: '{}', url: '{}']", news.title(), news.url());

                if (!used.isEmpty()) {
                    List<Path> images = PDFImage.create(new URL(news.url()));
                    for (Path image : images) {
                        logger.info("Successfully convert the image [path: '{}']", image);
                    }
                    tweet(token, news.url(), news.title(), images);
                }

                writer.write(news.url());
                writer.newLine();
                writer.flush();
            }

            token.save();
        } catch (Exception e) {
            logger.error("Failed.", e);
            System.exit(1);
        }

        logger.info("End {}", Main.class);
    }

    private static void tweet(TwitterToken token, String url, String title, List<Path> images) throws TwitterException, ApiException {
        // 画像のアップロードは、Twitter4j (v1) で行う必要がある
        Twitter twitter = Twitter.newBuilder()
                .oAuthConsumer(
                        token.getOAuthConsumerKey(),
                        token.getOAuthConsumerSecret()
                )
                .oAuthAccessToken(
                        token.getOAuthAccessToken(),
                        token.getOAuthAccessTokenSecret()
                )
                .build();
        TweetsResources tweets = twitter.v1().tweets();

        // ツイートは、twitter-api-java-sdk で行う必要がある
        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(
                token.getOAuth2ClientID(),
                token.getOAuth2ClientSecret(),
                token.getOAuth2AccessToken(),
                token.getOAuth2RefreshToken(),
                true
        );
        TwitterApi api = new TwitterApi(credentials);

        // 4枚ずつに分割
        List<List<Path>> list = new ArrayList<>();
        List<Path> current = null;
        for (int i = 0; i < images.size(); i++) {
            if (i % 4 == 0) {
                current = new ArrayList<>();
                list.add(current);
            }
            current.add(images.get(i));
        }

        String prevId = null;
        for (int i = 0; i < list.size(); i++) {
            TweetCreateRequestMedia requestMedia = new TweetCreateRequestMedia();
            for (Path image : list.get(i)) {
                UploadedMedia media = tweets.uploadMedia(image.toFile());
                logger.info("Successfully upload the image. [id: " + media.getMediaId() + "].");

                requestMedia.addMediaIdsItem(Long.toString(media.getMediaId()));
            }

            TweetCreateRequest request = new TweetCreateRequest();
            if (list.size() == 1) {
                request.setText(String.format("%s\n%s", title, url));
            } else {
                request.setText(String.format("%s (%d/%d)\n%s", title, (i + 1), list.size(), url));
            }
            request.setMedia(requestMedia);

            if (prevId != null) {
                request.setReply(new TweetCreateRequestReply().inReplyToTweetId(prevId));
            }

            TweetCreateResponse result = api.tweets().createTweet(request).execute();
            if (result.getErrors() != null) {
                throw new ApiException(result.toJson());
            }

            TweetCreateResponseData data = result.getData();
            logger.info("Successfully updated the status to [" + data.getText() + "][" + data.getId() + "].");

            prevId = data.getId();
        }
    }
}
