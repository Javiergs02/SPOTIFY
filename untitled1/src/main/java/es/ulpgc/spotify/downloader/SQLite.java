package es.ulpgc.spotify.downloader;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLite{
    public static void main(String[] args) throws Exception {
        String dbPath = "C:/Users/jaorg/OneDrive/Documentos/Recursos/spotify.db";
        try(Connection connection = connect(dbPath)) {
            Statement statement = connection.createStatement();
            Main albums = new Main();
            Map<Integer, List<String>> album = albums.getArtistsAlbums();
            Map<Integer,List<String>> song =albums.getAlbumSongs();
            dropTable(statement);
            createTable(statement);
            insert(statement,album,song);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void delete(Statement statement) throws SQLException {
        statement.execute("DELETE FROM products\n" +
                "WHERE id=1;");
    }

    private static void createTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS Artists (" +
                "id INTEGER PRIMARY KEY,\n" +
                "name TEXT NOT NULL" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS Albums (" +
                "id INTEGER,\n" +
                "name TEXT NOT NULL" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS Songs (" +
                "id INTEGER ,\n" +
                "name TEXT NOT NULL" +
                ");");
    }

    private static void dropTable(Statement statement) throws SQLException {
        statement.execute("DROP TABLE IF EXISTS Artists;\n");
        statement.execute("DROP TABLE IF EXISTS Songs;\n");
        statement.execute("DROP TABLE IF EXISTS Albums;\n");
    }

    private static void insert(Statement statement, Map<Integer, List<String>> album, Map<Integer,List<String>> song) throws SQLException {
        for (int i = 0; i < album.size(); i++) {
            for (int j = 0; j < album.get(i).size(); j++) {
                statement.execute(String.format("INSERT INTO Albums (id,name)\n" +
                        "VALUES(%d, '%s');", i, album.get(i).get(j).replace("'","")));
            }
        }
        for (int i = 0; i < song.size(); i++) {
            for (int j = 0; j < song.get(i).size(); j++) {
                statement.execute(String.format("INSERT INTO Songs (id,name)\n" +
                        "VALUES(%d, '%s');", i, song.get(i).get(j).replace("'","")));
            }
        }
        statement.execute("INSERT INTO Artists (id,name)\n" +
                "VALUES(0, 'Trueno'),(1, 'Ozuna'), (2, 'Quevedo')," +
                " (3, 'Duki'), (4, 'Paulo Londra');");

    }


    public static Connection connect(String dbPath) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}

