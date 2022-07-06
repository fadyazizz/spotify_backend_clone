package scalable.com.music.commands;

import com.arangodb.entity.BaseEdgeDocument;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import com.arangodb.entity.BaseDocument;
import scalable.com.shared.classes.MinIo;
import scalable.com.shared.classes.Responder;
import javax.validation.constraints.NotBlank;
import scalable.com.music.constants.*;

public class GetSongNumberOfStreams extends MusicCommand {
    // @NotBlank(message = "song_id should not be empty")
    private String song_id;

    @Override
    public String getCommandName() {
        return "GetSongNumberOfStreams";
    }

    @Override
    public String execute() {
        BaseDocument res;
        Arango arango = Arango.getInstance();
        org.json.JSONObject response = new JSONObject();

        try {

            arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                    false);
            // extract song id
            this.song_id = body.getString("song_id");
            res = arango.readDocument(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION, song_id);
            if (res == null) {
                return Responder.makeErrorResponse("Song does not exist", 404);
            }

            response.put("number_of_streams", res.getAttribute("number_of_streams"));
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
        return false;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {

        try {
            // extract song id
            this.song_id = body.getString("song_id");

        } catch (Exception e) {
            System.out.println(body);
            throw new ValidationException("attributes data types are wrong: " + e.getMessage());
        }
    }
}