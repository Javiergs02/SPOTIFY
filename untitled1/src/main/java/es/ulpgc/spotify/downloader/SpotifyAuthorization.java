package es.ulpgc.spotify.downloader;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;

import static java.net.HttpURLConnection.HTTP_OK;

public class SpotifyAuthorization {
    private static final String ACCOUNT_BASE_URI = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_ID = "07cb9282f7374f708e6da943e4433d38";
    private static final String CLIENT_SECRET = "1657129fe7224f09a0cd0951625d978f";

    public static Token get() throws Exception {
        Token token = gson().fromJson(responseOf(request()), Token.class);
        token.updateExpiration();
        return token;
    }

    public static HttpRequest request() {
        return HttpRequest.newBuilder()
                .uri(URI.create(ACCOUNT_BASE_URI))
                .header("Authorization", "Basic " + SpotifyAuthorization.id())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
    }

    private static String id() {
        return Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
    }

    private static String responseOf(HttpRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HTTP_OK) throw new Exception(" Http response error " + response.statusCode());
        return response.body();
    }

    public static class Token {
        public final String access_token;
        public final String token_type;
        public final int expires_in;
        public Instant expiration;

        public Token(String access_token, String token_type, int expires_in) {
            this.access_token = access_token;
            this.token_type = token_type;
            this.expires_in = expires_in;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "access_token='" + access_token + '\'' +
                    ", token_type='" + token_type + '\'' +
                    ", expires_in=" + expires_in +
                    '}';
        }

        public void updateExpiration() {
            long epochSecond = Instant.now().getEpochSecond() + expires_in;
            expiration = Instant.ofEpochSecond(epochSecond);
        }

        public boolean isExpired() {
            return expiration.isBefore(Instant.now());
        }

        public boolean isValid() {
            return !isExpired();
        }
    }

    public static Gson gson() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return builder.create();
    }

}
