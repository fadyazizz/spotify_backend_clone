package scalable.com.music.commands;

import com.arangodb.entity.BaseDocument;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.shared.classes.Responder;

import java.util.UUID;

public class FavoriteAlbumCommand extends CommandVerifier {
    @Override
    public String getCommandName() {
        return "FavoriteAlbum";
    }

    @Override
    public String execute() {
        Arango arango = null;
        try {
            String album_id = body.getString("album_id");
            String user_id = this.tokenPayload.getString("id");

            // TODO:String UserId = authenticationParams.getString(ThreadCommand.USERNAME);
            // TODO: auto generated id             uu id

            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("spotifyArangoDb","FavoriteAlbums",false);
            BaseDocument myDocument = new BaseDocument();
            //TODO : key of document
            //  myDocument.setKey(id.toString());
            myDocument.addAttribute("user_id",user_id);
            myDocument.addAttribute("album_id", album_id);
            BaseDocument res = arango.createDocument("spotifyArangoDb","FavoriteAlbums", myDocument);
            return Responder.makeMsgResponse("Added Album to Favorites");
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
