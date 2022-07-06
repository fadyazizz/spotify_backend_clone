package scalable.com.music.commands;

import com.arangodb.entity.BaseDocument;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.shared.classes.Responder;

import java.util.UUID;

public class DeletePlaylistCommand extends CommandVerifier {
    @Override
    public String getCommandName() {
        return "DeletePlaylist";
    }

    @Override
    public String execute() {
        
            Arango arango = null;
            try {
                String key = body.getString("id");

                arango = Arango.getInstance();

                arango.deleteDocument("spotifyArangoDb","Playlists", key);
                return Responder.makeMsgResponse("Deleted Playlist");
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
