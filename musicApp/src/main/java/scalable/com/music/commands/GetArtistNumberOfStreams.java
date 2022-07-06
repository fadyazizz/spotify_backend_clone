package scalable.com.music.commands;

import com.arangodb.ArangoCursor;
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
import java.util.Collections;
import java.util.Map;
import scalable.com.music.constants.*;

public class GetArtistNumberOfStreams extends MusicCommand {
    // @NotBlank(message = "artist_id should not be empty")
    private String artist_id;
    private int number_of_streams;

    @Override
    public String getCommandName() {
        return "GetArtistNumberOfStreams";
    }

    @Override
    public String execute() {

        org.json.JSONObject response = new JSONObject();
        Arango arango = Arango.getInstance();
        try {

            arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                    false);
            // extract song id
            this.artist_id = body.getString("artist_id");

            String query = "FOR song IN Songs FILTER @artist_id IN song.artists RETURN song";
            Map<String, Object> bindVars = Collections.singletonMap("artist_id", this.artist_id);
            ArangoCursor<BaseDocument> cursor = arango.query(DatabaseConstants.DATABASE_NAME, query, bindVars);
            cursor.forEachRemaining(aDocument -> {
                number_of_streams += Integer.parseInt(aDocument.getAttribute("number_of_streams") + "");
            });

            response.put("number_of_streams", number_of_streams);

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
            this.artist_id = body.getString("artist_id");

        } catch (Exception e) {
            System.out.println(body);
            throw new ValidationException("attributes data types are wrong: " + e.getMessage());
        }
    }
}