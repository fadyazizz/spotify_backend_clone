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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import scalable.com.music.constants.*;

public class SearchSong extends MusicCommand {
    // @NotBlank(message = "artist_id should not be empty")
    private String search_for;

    @Override
    public String getCommandName() {
        return "SearchSong";
    }

    @Override
    public String execute() {

        org.json.JSONObject response = new JSONObject();
        org.json.JSONArray resultSongs = new JSONArray();
        Arango arango = Arango.getInstance();
        try {

            arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                    false);
            // extract song id
            this.search_for = body.getString("search_for");
            System.out.println(search_for);
            // String query = "FOR song IN Songs" +
            // " SEARCH ANALYZER(STARTS_WITH(song.name, \"%@search_for%\"), \"identity\")" +
            // " RETURN song";

            String query = "FOR song IN Songs FILTER @search_for IN song.genres OR @search_for == song.name RETURN song";

            Map<String, Object> bindVars = Collections.singletonMap("search_for", this.search_for);
            ArangoCursor<BaseDocument> cursor = arango.query(DatabaseConstants.DATABASE_NAME, query, bindVars);
            cursor.forEachRemaining(aDocument -> {
                org.json.JSONObject song = new JSONObject();
                song.put("name", aDocument.getAttribute("name"));
                song.put("id", aDocument.getKey());
                resultSongs.put(song);

            });

            response.put(DatabaseConstants.SONGS_COLLECTION, resultSongs);

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
            this.search_for = body.getString("search_for");

        } catch (Exception e) {
            System.out.println(body);
            throw new ValidationException("attributes data types are wrong: " + e.getMessage());
        }
    }
}