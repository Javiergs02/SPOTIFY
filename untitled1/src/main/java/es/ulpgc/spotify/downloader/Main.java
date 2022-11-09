package es.ulpgc.spotify.downloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private final static String ARTIST_1 = "2x7PC78TmgqpEIjaGAZ0Oz";
    private final static String ARTIST_2 = "1i8SpTcr7yvPOmcqrbnVXY";
    private final static String ARTIST_3 = "52iwsT98xCoGgiGntTiR7K";
    private final static String ARTIST_4 = "1bAftSH8umNcGZ0uyV7LMg";
    private final static String ARTIST_5 = "3vQ0GE3mI0dAaxIMYe5g7z";

    public static void main(String[] args) throws Exception {
        getArtistsAlbums();
        getAlbumSongs();
    }

    public static Map<Integer, List<String>> getArtistsAlbums() throws Exception {
        ArrayList<String> artists = new ArrayList<>();
        artists.add(ARTIST_1);
        artists.add(ARTIST_2);
        artists.add(ARTIST_3);
        artists.add(ARTIST_4);
        artists.add(ARTIST_5);
        SpotifyAccessor accessor = new SpotifyAccessor();
        Map<Integer, List<String>> map = new HashMap<>();
        for (int i = 0; i < artists.size(); i++) {
            String result = accessor.get(String.format("/artists/%s/albums", artists.get(i)), Map.of());
            JSONObject obj = new JSONObject(result);
            JSONArray items = (obj.getJSONArray("items"));
            for (int j = 0; j < items.length(); j++) {
                String album = items.getJSONObject(j).getString("name");
                if (map.containsKey(i)) {
                    map.get(i).add(album);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(album);
                    map.put(i, list);
                }
            }
        }
        return map;
    }

    public static Map<Integer, List<String>> getAlbumSongs() throws Exception {
        ArrayList<String> artists = new ArrayList<>();
        artists.add(ARTIST_1);
        artists.add(ARTIST_2);
        artists.add(ARTIST_3);
        artists.add(ARTIST_4);
        artists.add(ARTIST_5);
        SpotifyAccessor accessor = new SpotifyAccessor();
        Map<Integer, List<String>> map = new HashMap<>();
        for (int i = 0; i < artists.size(); i++) {
            String result = accessor.get(String.format("/artists/%s/albums", artists.get(i)), Map.of());
            JSONObject obj = new JSONObject(result);
            JSONArray items = (obj.getJSONArray("items"));
            for (int j = 0; j < items.length(); j++) {
                String id = items.getJSONObject(j).getString("id");
                String song = accessor.get(String.format("/albums/%s/tracks", id), Map.of());
                JSONObject obj1 = new JSONObject(song);
                JSONArray key = (obj1.getJSONArray("items"));
                //System.out.println(key);
                for (int k = 0; k < key.length(); k++) {
                    String name = key.getJSONObject(k).getString("name");
                    //System.out.println(name);
                    if (map.containsKey(i)) {
                        map.get(i).add(name);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(name);
                        map.put(i, list);
                    }
                }
            }
        }
        return map;
    }
}



