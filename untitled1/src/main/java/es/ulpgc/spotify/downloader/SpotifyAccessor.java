package es.ulpgc.spotify.downloader;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.StringJoiner;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SpotifyAccessor {
    private final static String API_BASE_URI = "https://api.spotify.com/v1";

    private final HttpClient client;
    private SpotifyAuthorization.Token authorization;

    public SpotifyAccessor() throws Exception {
        client = HttpClient.newHttpClient();
        authorization = SpotifyAuthorization.get();
    }

    private void checkAuthorization() throws Exception {
        if (authorization.isValid()) return;
        System.out.println("Token has expired. Requesting other...");
        authorization = SpotifyAuthorization.get();
    }

    public String get(String path, Map<String, String> params) throws Exception {
        return responseOf(request(path, params).GET().build());
    }

    private HttpRequest.Builder request(String path, Map<String, String> queryParams) throws Exception {
        checkAuthorization();
        return HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URI + path + with(queryParams)))
                .header("Authorization", authorization.token_type + " " + authorization.access_token)
                .header("Content-Type", "application/json");
    }

    private String with(Map<String, String> queryParams) {
        if (queryParams.isEmpty()) return "";
        StringJoiner result = new StringJoiner("&");
        for (Map.Entry<String, String> entry : queryParams.entrySet())
            result.add(encode(entry));
        return "?" + result;
    }

    private String encode(Map.Entry<String, String> entry) {
        return String.format("%s=%s",
                URLEncoder.encode(entry.getKey(), UTF_8),
                URLEncoder.encode(entry.getValue(), UTF_8)
        );
    }

    private String responseOf(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HTTP_OK) throw new Exception(" Http response error " + response.statusCode());
        return response.body();
    }




}
