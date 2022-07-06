package scalable.com.music.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import com.arangodb.entity.BaseDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scalable.com.shared.classes.Arango;
import scalable.com.music.MusicApp;
import scalable.com.shared.testsHelper.TestHelper;
import scalable.com.music.constants.*;

public class MusicTest {
    private String artist_id = "1", user_id = "1", song_id = "39710";
    private static Arango arango;
    public static String user_id_1 = "1";
    public static boolean is_artist_1 = false;

    public static String user_id_2 = "2";
    public static boolean is_artist_2 = true;

    public static String track_id = "1";
    public static String playlist_name = "playlist6";
    public static String playlist_id = "3";
    public static String album_id = "3";
    public static String created_playlist1 = "";
    public static String created_playlist2 = "";

    @BeforeClass
    public static void setUp() {
        try {

            arango = Arango.getConnectedInstance();
            MusicApp app = new MusicApp();
            app.start();
            TestHelper.appBeingTested = app;
            arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                    false);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,
                    DatabaseConstants.PLAYLIST_COLLECTION, false);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,
                    DatabaseConstants.FAVORITE_ALBUMS_COLLECTION, false);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,
                    DatabaseConstants.FAVORITE_TRACKS_COLLECTION, false);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // STREAMING APP TESTS~~~~~~~~~~~~~~~~~~~~
    public static String createSong(String name, String artists, String genres, String album_id, boolean isArtist) {
        JSONObject body = new JSONObject();
        body.put("name", name);
        body.put("artists", artists);
        body.put("genres", genres);
        body.put("album_id", album_id);

        JSONObject uriParams = new JSONObject();
        JSONObject files = new JSONObject();
        JSONObject dummyData = new JSONObject();
        dummyData.put("data", "FILE DATA");
        dummyData.put("type", "FILE TYPE");
        files.put("song", dummyData);
        files.put("cover", dummyData);
        JSONObject token = new JSONObject();
        if (isArtist)
            token.put("isArtist", "true");
        else
            token.put("isArtist", "false");
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        request.put("files", files);

        CreateSongCommand createSong = new CreateSongCommand();
        return TestHelper.execute(createSong, request);
    }

    public static String getSong(String song_id, String user_id, boolean isPremium) {
        JSONObject body = new JSONObject();
        body.put("song_id", song_id);

        JSONObject uriParams = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", user_id);
        token.put("isPremium", isPremium);
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);

        GetSong getSong = new GetSong();
        return TestHelper.execute(getSong, request);
    }

    public static String getSongNumberOfStreams(String song_id) {
        JSONObject body = new JSONObject();
        body.put("song_id", song_id);

        JSONObject uriParams = new JSONObject();
        JSONObject token = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", false);
        request.put("tokenPayload", token);

        GetSongNumberOfStreams getSongNumberOfStreams = new GetSongNumberOfStreams();
        return TestHelper.execute(getSongNumberOfStreams, request);
    }

    public static String getArtistNumberOfStreams(String artist_id) {
        JSONObject body = new JSONObject();
        body.put("artist_id", artist_id);

        JSONObject uriParams = new JSONObject();
        JSONObject token = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", false);
        request.put("tokenPayload", token);

        GetArtistNumberOfStreams getArtistNumberOfStreams = new GetArtistNumberOfStreams();
        return TestHelper.execute(getArtistNumberOfStreams, request);
    }

    public static String searchSong(String search_for) {
        JSONObject body = new JSONObject();
        body.put("search_for", search_for);

        JSONObject uriParams = new JSONObject();
        JSONObject token = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", false);
        request.put("tokenPayload", token);

        SearchSong searchSong = new SearchSong();
        return TestHelper.execute(searchSong, request);
    }

    public static String RequestSimulatorCreatePlaylist(String playlist_name) {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "2");
        body.put("name", playlist_name);
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);

        CreatePlaylistCommand createPlaylistCommand = new CreatePlaylistCommand();
        return TestHelper.execute(createPlaylistCommand,request);
    }

    // MUSIC APP TESTS~~~~~~~~~~~~~~~~~~~~
    public static String  RequestSimulatorDeletePlaylist(String created_playlist1) {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "2");
        body.put("id", created_playlist1);
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);

        DeletePlaylistCommand deletePlaylistCommand = new DeletePlaylistCommand();
        return TestHelper.execute(deletePlaylistCommand,request);
    }

    public static String RequestSimulatorFavoriteAlbum(String album_id) {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "2");
        body.put("album_id", album_id);
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        FavoriteAlbumCommand favoriteAlbumCommand = new FavoriteAlbumCommand();
        return TestHelper.execute(favoriteAlbumCommand,request);    }

    public static String RequestSimulatorFavoriteTrack(String track_id) {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "1");
        body.put("track_id", track_id);
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        FavoriteTrackCommand favoriteTrackCommand = new FavoriteTrackCommand();
        return TestHelper.execute(favoriteTrackCommand,request);
    }

    public static String RequestSimulatorPlaylistVisibility(String created_playlist2) {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        body.put("id", created_playlist2);
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        PlaylistVisibilityCommand playlistVisibilityCommand = new PlaylistVisibilityCommand();
        return TestHelper.execute(playlistVisibilityCommand,request);
    }

    public static String RequestSimulatorViewFavoriteTracks() {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "1");
        request.put("body", body);
        request.put("methodType", "GET");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        ViewFavoriteTracksCommand viewFavoriteTracks = new ViewFavoriteTracksCommand();
        return TestHelper.execute(viewFavoriteTracks,request);
    }

    public static String RequestSimulatorViewFavoriteAlbums() {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "2");
        request.put("body", body);
        request.put("methodType", "GET");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        ViewFavoriteAlbumsCommand viewFavoriteAlbums = new ViewFavoriteAlbumsCommand();
        return TestHelper.execute(viewFavoriteAlbums,request);
    }

    public static String RequestSimulatorFavoriteAlbumsOfOthers() {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        uriParams.put("user_id", "2");
        request.put("body", body);
        request.put("methodType", "GET");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        FavoriteAlbumsOfOthersCommand favoriteAlbumsOfOthers = new FavoriteAlbumsOfOthersCommand();
        return TestHelper.execute(favoriteAlbumsOfOthers,request);
    }

    public static String RequestSimulatorFavoriteTracksOfOthers() {
        JSONObject body = new JSONObject();
        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject token = new JSONObject();
        uriParams.put("user_id", "1");
        token.put("id", "2");
        request.put("body", body);
        request.put("methodType", "GET");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);
        System.out.println("Request " + request.toString());
        FavoriteTracksOfOthersCommand favoriteTracksOfOthers = new FavoriteTracksOfOthersCommand();
        return TestHelper.execute(favoriteTracksOfOthers,request);
    }

    public static JSONObject makeRequest(JSONObject body, String methodType, JSONObject uriParams,
            boolean isAuthenticated, JSONObject tokenPayload) {
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", methodType);
        request.put("uriParams", uriParams);
        JSONObject authenticationParams = new JSONObject();
        request.put("isAuthenticated", isAuthenticated);
        request.put("tokenPayload", tokenPayload);

        request.put("authenticationParams", authenticationParams);
        return request;
    }

    public static String ratePlayList(String playlist_id, int rating) {
        JSONObject body = new JSONObject();
        body.put("playlist_id", playlist_id);
        body.put("rating", rating);
        JSONObject uriParams = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "2");
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);

        RatePlaylistCommand ratePlaylistCommand = new RatePlaylistCommand();
        return TestHelper.execute(ratePlaylistCommand, request);
    }

    public static String rateMusicTrack(String song_id, int rating) {
        JSONObject body = new JSONObject();
        body.put("song_id", song_id);
        body.put("rating", rating);
        JSONObject uriParams = new JSONObject();
        JSONObject token = new JSONObject();
        token.put("id", "2");
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);

        RateMusicTrackCommand rateMusicTrackCommand = new RateMusicTrackCommand();
        return TestHelper.execute(rateMusicTrackCommand, request);
    }

    // STREAMING APP TESTS~~~~~~~~~~~~~~~~~~~~
    @Test
    public void createSongAsArtistTest() {
        int beforeCount = arango.documentCount(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION);
        String response = createSong("Song1", "1,2", "Rock,Pop", "1", true);
        JSONObject responseJson = new JSONObject(response);
        int afterCount = arango.documentCount(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION);
        assertEquals(200, responseJson.getInt("statusCode"));
        assertEquals(beforeCount + 1, afterCount);
    }
    @Test
    public void ratePlaylistTest() {
        String playlist = RequestSimulatorCreatePlaylist(playlist_name);
        System.out.println(playlist);

        JSONObject response_1 =new JSONObject(playlist);
        JSONObject data = response_1.getJSONObject("data");
        System.out.println(data);
        String key = data.getString("_key");
        created_playlist1 = key;

        String response = ratePlayList(created_playlist1, 5);
        JSONObject responseJson = new JSONObject(response);
        assertEquals("the new rating is:" + 5.0, responseJson.getString("msg"));
    }
    @Test
    public void ratePlaylistAlreadyRatedTest() {

        String playlist = RequestSimulatorCreatePlaylist(playlist_name);
        System.out.println(playlist);

        JSONObject response_1 =new JSONObject(playlist);
        JSONObject data = response_1.getJSONObject("data");
        System.out.println(data);
        String key = data.getString("_key");
        created_playlist1 = key;

        String response = ratePlayList(created_playlist1, 5);
        response = ratePlayList(created_playlist1, 5);
        JSONObject responseJson = new JSONObject(response);
        assertEquals("you have already rated this playlist", responseJson.getString("msg"));
    }
    @Test
    public void rateMusicTrackTest() {
        String song_response = createSong("Song1", "1,2", "Rock,Pop", "1", true);
        JSONObject song_responseJson = new JSONObject(song_response);
        String temp_song_id =(song_responseJson.getJSONObject("data")).get("id")+"";
        System.out.println(temp_song_id);
        String response = rateMusicTrack(temp_song_id, 5);
        JSONObject responseJson = new JSONObject(response);
        assertEquals("the new rating is:" + 5.0, responseJson.getString("msg"));
    }
    @Test
    public void rateMusicTrackAlreadyRatedTest() {
        String song_response = createSong("Song1", "1,2", "Rock,Pop", "1", true);
        JSONObject song_responseJson = new JSONObject(song_response);
        String temp_song_id =(song_responseJson.getJSONObject("data")).get("id")+"";

        String response = rateMusicTrack(temp_song_id, 5);
        response = rateMusicTrack(temp_song_id, 5);
        JSONObject responseJson = new JSONObject(response);
        assertEquals("you have already rated this song", responseJson.getString("msg"));
    }


    @Test
    public void createSongAsNonArtistTest() {
        int beforeCount = arango.documentCount(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION);
        String response = createSong("Song1", "1,2", "Rock,Pop", "1", false);
        JSONObject responseJson = new JSONObject(response);
        int afterCount = arango.documentCount(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION);
        assertEquals("Only artists can create songs", responseJson.getString("msg"));
        assertEquals(beforeCount, afterCount);
    }

    @Test
    public void getSongNumberOfStreamsTest() {

        String song_response = createSong("Song1", "1,2", "Rock,Pop", "1", true);
        JSONObject song_responseJson = new JSONObject(song_response);
        String temp_song_id =(song_responseJson.getJSONObject("data")).get("id")+"";

        String response = getSongNumberOfStreams(temp_song_id);
        JSONObject responseJson = new JSONObject(response);
        BaseDocument songExist = arango.readDocument(DatabaseConstants.DATABASE_NAME,
                DatabaseConstants.SONGS_COLLECTION, temp_song_id);
        if (songExist == null) {
            assertEquals(404, responseJson.getInt("statusCode"));

        } else {
            assertEquals(200, responseJson.getInt("statusCode"));

        }
    }

    @Test
    public void getArtistNumberOfStreamsTest() {
        String response = getArtistNumberOfStreams(artist_id);
        JSONObject responseJson = new JSONObject(response);
        assertEquals(200, responseJson.getInt("statusCode"));
    }

    @Test
    public void getSongTest() {

        String song_response = createSong("Song1", "1,2", "Rock,Pop", "1", true);
        JSONObject song_responseJson = new JSONObject(song_response);
        String temp_song_id =(song_responseJson.getJSONObject("data")).get("id")+"";
        BaseDocument song = arango.readDocument(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                temp_song_id);
        int streams_before = Integer.parseInt(song.getAttribute("number_of_streams") + "");
        String response = getSong(temp_song_id, user_id, true);
        
        song = arango.readDocument(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION, temp_song_id);
        int streams_after = Integer.parseInt(song.getAttribute("number_of_streams") + "");

        JSONObject responseJson = new JSONObject(response);
        assertEquals(200, responseJson.getInt("statusCode"));
        assertEquals(streams_before + 1, streams_after);
    }

    @Test
    public void searchSongTest() {
        String response = searchSong("Yellow");
        JSONObject responseJson = new JSONObject(response);
        assertEquals(200, responseJson.getInt("statusCode"));
    }

    // MUSIC APP TESTS~~~~~~~~~~~~~~~~~~~~
    @Test
    public void FavoriteTrack() {
        String Track = RequestSimulatorFavoriteTrack(track_id);
        JSONObject response = new JSONObject(Track);

        System.out.println(response);
        assert response.getInt("statusCode") == 200;
        assert response.getString("msg").equals("Added Track to Favorites");
    }

    @Test
    public void FavoriteTracksOfOthers() {
        String Tracks = RequestSimulatorFavoriteTracksOfOthers();
        JSONObject response = new JSONObject(Tracks);

        JSONObject data = new JSONObject();
        JSONObject correct = new JSONObject();
        ArrayList<String> newdata = new ArrayList<>();
        newdata.add(track_id);
        data.put("track_id", newdata);
        correct.put("data", data);
        System.out.println(response.getJSONObject("data"));
        System.out.println(data);
        System.out.println(response);
        assert response.getInt("statusCode") == 200;
        assert response.getJSONObject("data").toString().equals(data.toString());

    }

    @Test
    public void FavoriteAlbum() {
        String Album = RequestSimulatorFavoriteAlbum(album_id);
        JSONObject response = new JSONObject(Album);

        System.out.println(response);
        assert response.getInt("statusCode") == 200;
        assert response.getString("msg").equals("Added Album to Favorites");
    }

    @Test
    public void FavoriteAlbumsOfOthers() {
        String Albums = RequestSimulatorFavoriteAlbumsOfOthers();
        JSONObject response = new JSONObject(Albums);

        JSONObject data = new JSONObject();
        JSONObject correct = new JSONObject();
        ArrayList<String> newdata = new ArrayList<>();
        newdata.add(album_id);
        data.put("album_id", newdata);
        correct.put("data", data);
        System.out.println(response.getJSONObject("data"));
        System.out.println(data);
        System.out.println(response);
        assert response.getInt("statusCode") == 200;
        assert response.getJSONObject("data").toString().equals(data.toString());
    }

    @Test
    public void CreatePlaylist() {
        String response = RequestSimulatorCreatePlaylist(playlist_name);
        System.out.println(response);
        assert new JSONObject(response).getInt("statusCode") == 200;
    }

    @Test
    public void DeletePlaylist() {
        String playlist = RequestSimulatorCreatePlaylist(playlist_name);
        System.out.println(playlist);

        JSONObject response_1 =new JSONObject(playlist);
        JSONObject data = response_1.getJSONObject("data");
        System.out.println(data);
        String key = data.getString("_key");
        created_playlist1 = key;
        String response = RequestSimulatorDeletePlaylist(created_playlist1);
        System.out.println(response);
        assert new JSONObject(response).getInt("statusCode") == 200;
        assert new JSONObject(response).getString("msg").equals("Deleted Playlist");
    }

    @Test
    public void PlaylistVisibility() {
        String playlist = RequestSimulatorCreatePlaylist(playlist_name);
        JSONObject response_1 = new JSONObject(playlist);
        JSONObject data = response_1.getJSONObject("data");
        String key = data.getString("_key");
        created_playlist1 = key;
        String response = RequestSimulatorPlaylistVisibility(created_playlist1);
        System.out.println(response);
        assert new JSONObject(response).getInt("statusCode") == 200;
        assert new JSONObject(response).getString("msg").equals("UpdatedPlaylist");
    }

    // response is an object
    @Test
    public void ViewFavoriteAlbums() {
        String Albums = RequestSimulatorViewFavoriteAlbums();
        JSONObject response = new JSONObject(Albums);
        JSONObject data = new JSONObject();
        JSONObject correct = new JSONObject();
        ArrayList<String> newdata = new ArrayList<>();
        newdata.add(album_id);
        data.put("album_id", newdata);
        correct.put("data", data);
        System.out.println(response.getJSONObject("data"));
        System.out.println(data);
        System.out.println(response);
        assert response.getInt("statusCode") == 200;
        assert response.getJSONObject("data").toString().equals(data.toString());
    }

    // response is an object
    @Test
    public void ViewFavoriteTracks() {
        String Tracks = RequestSimulatorViewFavoriteTracks();
        JSONObject response = new JSONObject(Tracks);

        JSONObject data = new JSONObject();
        JSONObject correct = new JSONObject();
        String[] tracks = { "1" };
        data.put("track_id", tracks);
        correct.put("data", data);
        System.out.println(response);
        System.out.println(data);
        assert response.getInt("statusCode") == 200;
        assert response.getJSONObject("data").toString().equals(data.toString());
    }

    @AfterClass
    public static void dropAllCollections() {
        System.out.println("In after test");

        arango.dropCollection(DatabaseConstants.DATABASE_NAME, DatabaseConstants.PLAYLIST_COLLECTION);
        arango.dropCollection(DatabaseConstants.DATABASE_NAME, DatabaseConstants.FAVORITE_ALBUMS_COLLECTION);
        arango.dropCollection(DatabaseConstants.DATABASE_NAME, DatabaseConstants.FAVORITE_TRACKS_COLLECTION);

    }
}