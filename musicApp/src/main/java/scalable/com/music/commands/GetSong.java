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

public class GetSong extends MusicCommand {
    // @NotBlank(message = "song_id should not be empty")
    private String song_id;
    private boolean canSkip = false;
    private boolean hasAds = true;

    @Override
    public String getCommandName() {
        return "GetSong";
    }

    @Override
    public String execute() {
        BaseDocument res;
        Arango arango = Arango.getInstance();
        JSONObject response = new JSONObject();

        try {
            if (this.tokenPayload == null)
                return Responder.makeErrorResponse("No token provided", 404);

            arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
            arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION,
                    false);
            // extract song id
            this.song_id = body.getString("song_id");

            // CHECK IF USER IS PREMIUM
            // JSONObject isPremiumRes =
            // App.communicateWithApp("Music","User",this.origRequest,"GET","IsSubscribedToPremium",null,null);
            // if(((JSONObject)isPremiumRes.get("data")).get("isSubscribed").equals(true)){
            // canSkip=true;
            // hasAds=false;
            // }

            if (this.tokenPayload.get("isPremium").equals("true")) {
                canSkip = true;
                hasAds = false;
            }
            // GET SONG
            res = arango.readDocument(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION, song_id);

            // UPDATE SONG
            int new_number_of_streams = 1 + Integer.parseInt(res.getAttribute("number_of_streams") + "");
            res.updateAttribute("number_of_streams", new_number_of_streams);
            arango.updateDocument(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION, res,
                    res.getKey());

            // CREATE JSON RESPONSE
            res.getProperties().forEach((key, value) -> response.put(key, value));
            response.put("can_skip", canSkip);
            response.put("has_ads", hasAds);

            // SEND TO RECOMMENDATIONS APP
            (this.origRequest.getJSONObject("body")).put("track_id", this.song_id);
            App.sendMessageToApp("recommendations", this.origRequest, "POST", "CreateMusicEdge", null, null);

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
            // extract song id
            this.song_id = body.getString("song_id");

        } catch (Exception e) {
            System.out.println(body);
            throw new ValidationException("attributes data types are wrong: " + e.getMessage());
        }
    }
}