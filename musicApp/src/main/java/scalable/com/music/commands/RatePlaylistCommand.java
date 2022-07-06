package scalable.com.music.commands;
import com.arangodb.entity.BaseDocument;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.PostgresConnection;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class RatePlaylistCommand extends MusicCommand {
    @Override
    public String getCommandName() {
        return "RatePlaylist";
    }
    @Override
    public String execute(){
        Arango arango = null;
        try{
            String playlistIdentifier = body.getString("playlist_id");
            int userId = Integer.parseInt(this.tokenPayload.getString("id"));
            int userRating = body.getInt("rating");

            arango = Arango.getInstance();

            BaseDocument toRead = arango.readDocument("spotifyArangoDb",
                    "Playlists", playlistIdentifier);
            if(toRead == (null)){
                return Responder.makeMsgResponse("This playlist does not exist");
            }
            Double oldRating = (Double)(toRead.getAttribute("Rating"));
            int totalRatings = (Integer)(toRead.getAttribute("number_times_rated"));
            ArrayList<Integer> peopleRated = (ArrayList<Integer>)
                    (toRead.getAttribute("people_rated"));
            if(peopleRated.contains(userId)) {
                return Responder.makeMsgResponse("you have already rated this playlist");
            }
            else{
                Double newRating = (oldRating*totalRatings+userRating)/(totalRatings+1);
                int newtotalRatings = totalRatings +1;
                peopleRated.add(userId);
                toRead.updateAttribute("Rating", newRating);
                toRead.updateAttribute("number_times_rated", newtotalRatings);
                toRead.updateAttribute("people_rated", peopleRated);

                arango.updateDocument("spotifyArangoDb", "Playlists", toRead, playlistIdentifier);

                return Responder.makeMsgResponse("the new rating is:" + newRating);

            }
        }
        catch (Exception e) {
            return Responder.makeErrorResponse(e.getMessage(), 404);
        }

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

    }
}