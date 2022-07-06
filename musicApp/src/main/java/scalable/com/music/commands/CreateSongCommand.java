package scalable.com.music.commands;

import com.arangodb.entity.BaseEdgeDocument;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import com.arangodb.entity.BaseDocument;
import scalable.com.shared.classes.MinIo;
import scalable.com.shared.classes.Responder;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import scalable.com.music.constants.*;

public class CreateSongCommand extends MusicCommand {
    // @NotBlank(message = "name should not be empty")
    private String name;
    // @NotBlank(message = "artists should not be empty")
    private String[] artists;
    // @NotBlank(message = "genres should not be empty")
    private String[] genres;
    // @NotBlank(message = "album_id should not be empty")
    private String album_id;

    @Override
    public String getCommandName() {
        return "CreateSong";
    }

    @Override
    public String execute() {

        Arango arango = Arango.getInstance();
        JSONObject response = new JSONObject();

        try {
            if (this.tokenPayload == null)
                return Responder.makeErrorResponse("No token provided", 404);
            if (this.tokenPayload.get("isArtist").equals("false"))
                return Responder.makeErrorResponse("Only artists can create songs", 404);

            arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                    false);
            // extract name
            this.name = body.getString("name");
            // extract album id
            this.album_id = body.getString("album_id");
            // extract artists
            this.artists = body.getString("artists").split(",");
            // extract album id
            this.genres = body.getString("genres").split(",");

            // name for song/cover files
            String coverAndAudioName = body.getString("name") + body.getString("artists");

            // UPLOADING SONG
            String songUrl;
            try {
                songUrl = MinIo.uploadObject("music-app-songbucket", coverAndAudioName, files.getJSONObject("song"));
                if (songUrl.isEmpty())
                    return Responder.makeErrorResponse("Error Occurred While Uploading Your Song!", 404);
            } catch (Exception e) {
                return Responder.makeErrorResponse(e.getMessage(), 400);
            }
            // UPLOADING COVER
            String coverUrl;
            try {
                coverUrl = MinIo.uploadObject("music-app-coverbucket", coverAndAudioName, files.getJSONObject("cover"));
                if (coverUrl.isEmpty())
                    return Responder.makeErrorResponse("Error Occurred While Uploading Your Cover Image!", 404);
            } catch (Exception e) {
                return Responder.makeErrorResponse(e.getMessage(), 400);
            }

            BaseDocument myDocument = new BaseDocument();
            myDocument.addAttribute("name", this.name);
            myDocument.addAttribute("artists", this.artists);
            myDocument.addAttribute("genres", this.genres);
            myDocument.addAttribute("album_id", this.album_id);
            myDocument.addAttribute("audio", songUrl);
            myDocument.addAttribute("cover", coverUrl);

            // Default attributes
            Double raate = 0.0;
            myDocument.addAttribute("rating", raate);
            myDocument.addAttribute("number_times_rated", 0);
            myDocument.addAttribute("people_rated", new ArrayList<Integer>());
            myDocument.addAttribute("number_of_streams", 0);
            myDocument.addAttribute("likes", 0);

            BaseDocument res = arango.createDocument(DatabaseConstants.DATABASE_NAME,
                    DatabaseConstants.SONGS_COLLECTION, myDocument);

            // CREATE JSON RESPONSE
            res.getProperties().forEach((key, value) -> response.put(key, value));
            response.put("id", res.getKey());

            // SEND TO RECOMMENDATIONS APP
            (this.origRequest.getJSONObject("body")).put("track_id", res.getKey());
            (this.origRequest.getJSONObject("body")).put("track_name", this.name);
            App.sendMessageToApp("recommendations", this.origRequest, "POST", "CreateMusicTrackNode", null, null);

        } catch (Exception e) {
            return Responder.makeErrorResponse(e.getMessage(), 404);
        }
        return Responder.makeDataResponse(response);
    }

    @Override
    public String getRestAPIMethod() {
        return "POST";
    }

    @Override
    public boolean isAuthNeeded() {
        return true;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {

        try {
            // extract name
            this.name = body.getString("name");
            // extract album id
            this.album_id = body.getString("album_id");
            // extract artists
            this.artists = body.getString("artists").split(",");
            // extract album id
            this.genres = body.getString("genres").split(",");

        } catch (Exception e) {
            System.out.println(body);
            throw new ValidationException("attributes data types are wrong: " + e.getMessage());
        }
    }
}