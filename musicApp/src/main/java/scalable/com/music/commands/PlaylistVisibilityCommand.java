package scalable.com.music.commands;

import com.arangodb.entity.BaseDocument;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.shared.classes.Responder;

public class PlaylistVisibilityCommand extends CommandVerifier {
    @Override
    public String getCommandName() {
        return "PlaylistVisibility";

    }

    @Override
    public String execute() {
        Arango arango = null;
        try {
            String key = body.getString("id");
            arango = Arango.getInstance();
            BaseDocument oldDoc = arango.readDocument("spotifyArangoDb","Playlists", key) ;
            if((Boolean) oldDoc.getAttribute("isVisible")){          // if true set to false and vice versa
                oldDoc.updateAttribute("isVisible",false);
            }
            else {
                oldDoc.updateAttribute("isVisible",true);
            }
            arango.updateDocument("spotifyArangoDb","Playlists",oldDoc,key);
            return Responder.makeMsgResponse("UpdatedPlaylist");


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
