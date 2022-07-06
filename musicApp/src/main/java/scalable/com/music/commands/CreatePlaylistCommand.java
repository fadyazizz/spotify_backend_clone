package scalable.com.music.commands;

import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import com.arangodb.entity.BaseDocument;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import scalable.com.shared.classes.Responder;


public class CreatePlaylistCommand extends CommandVerifier {
    @Override
    public String getCommandName() {
        return "CreatePlaylist";
    }

    @Override
    public String execute() {
        Arango arango = null;
         try {
             //UUID id = UUID.randomUUID();
            String name = body.getString("name");
            int user_id = Integer.parseInt(this.tokenPayload.getString("id"));
            // String user_id = body.getString("id");
            Boolean isVisible = true;
            Double rating = 0.0;
            ArrayList<String> song_ids = new ArrayList<>();
            int number_times_rated = 0;
            ArrayList<Integer> people_rated = new ArrayList<>();
        
            // TODO:String UserId = authenticationParams.getString(ThreadCommand.USERNAME);
            // TODO: auto generated id             uu id

            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("spotifyArangoDb","Playlists",false);
            BaseDocument myDocument = new BaseDocument();
            //TODO : key of document
          //  myDocument.setKey(id.toString());
           myDocument.addAttribute("name",name);
            myDocument.addAttribute("isVisible", isVisible);
            myDocument.addAttribute("Rating", rating);
            myDocument.addAttribute("user_id", user_id);
            myDocument.addAttribute("song_ids", song_ids);
            myDocument.addAttribute("number_times_rated", number_times_rated);
            myDocument.addAttribute("people_rated",people_rated);
           BaseDocument res = arango.createDocument("spotifyArangoDb","Playlists", myDocument);
           Map fordoc = res.getProperties();

           JSONObject f = new JSONObject(fordoc);
           f.put("_key" , res.getKey());
            return Responder.makeDataResponse(f);
        } catch (Exception e) {
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
