package yuji.software.saizeriya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class TwitterToken {
    private static final Path FILE = Path.of("twitter.properties");

    private static final String OAUTH_CONSUMER_KEY = "TWITTER_OAUTH_CONSUMER_KEY";

    private static final String OAUTH_CONSUMER_SECRET = "TWITTER_OAUTH_CONSUMER_SECRET";

    private static final String OAUTH_ACCESS_TOKEN = "TWITTER_OAUTH_ACCESS_TOKEN";

    private static final String OAUTH_ACCESS_TOKEN_SECRET = "TWITTER_OAUTH_ACCESS_TOKEN_SECRET";

    private static final String OAUTH2_CLIENT_ID = "TWITTER_OAUTH2_CLIENT_ID";

    private static final String OAUTH2_CLIENT_SECRET = "TWITTER_OAUTH2_CLIENT_SECRET";

    private static final String OAUTH2_ACCESS_TOKEN = "TWITTER_OAUTH2_ACCESS_TOKEN";

    private static final String OAUTH2_REFRESH_TOKEN = "TWITTER_OAUTH2_REFRESH_TOKEN";

    private final Properties properties;

    private TwitterToken(Properties properties) {
        this.properties = properties;
    }

    public static TwitterToken load() throws IOException {
        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        return new TwitterToken(properties);
    }

    public String getOAuthConsumerKey() {
        return properties.getProperty(OAUTH_CONSUMER_KEY);
    }

    public String getOAuthConsumerSecret() {
        return properties.getProperty(OAUTH_CONSUMER_SECRET);
    }

    public String getOAuthAccessToken() {
        return properties.getProperty(OAUTH_ACCESS_TOKEN);
    }

    public String getOAuthAccessTokenSecret() {
        return properties.getProperty(OAUTH_ACCESS_TOKEN_SECRET);
    }

    public String getOAuth2ClientID() {
        return properties.getProperty(OAUTH2_CLIENT_ID);
    }

    public String getOAuth2ClientSecret() {
        return properties.getProperty(OAUTH2_CLIENT_SECRET);
    }

    public String getOAuth2AccessToken() {
        return properties.getProperty(OAUTH2_ACCESS_TOKEN);
    }

    public void setOAuth2AccessToken(String accessToken) {
        properties.setProperty(OAUTH2_ACCESS_TOKEN, accessToken);
    }

    public String getOAuth2RefreshToken() {
        return properties.getProperty(OAUTH2_REFRESH_TOKEN);
    }

    public void setOAuth2RefreshToken(String refreshToken) {
        properties.setProperty(OAUTH2_REFRESH_TOKEN, refreshToken);
    }

    public void save() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            properties.store(writer, "");
        }
    }
}
