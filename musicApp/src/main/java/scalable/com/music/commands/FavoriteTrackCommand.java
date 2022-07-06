package scalable.com.music.commands;

import com.arangodb.entity.BaseDocument;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.shared.classes.Responder;

public class FavoriteTrackCommand extends CommandVerifier {
    @Override
    public String getCommandName() {
        return "FavoriteTrack";
    }

    @Override
    public String execute() {
        Arango arango = null;
        try {
            String track_id = body.getString("track_id");
            String user_id = this.tokenPayload.getString("id");

            // TODO:String UserId = authenticationParams.getString(ThreadCommand.USERNAME);
            // TODO: auto generated id             uu id

            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("spotifyArangoDb","FavoriteTracks",false);
            BaseDocument myDocument = new BaseDocument();
            //TODO : key of document
            //  myDocument.setKey(id.toString());
            myDocument.addAttribute("user_id",user_id);
            myDocument.addAttribute("track_id", track_id);
            BaseDocument res = arango.createDocument("spotifyArangoDb","FavoriteTracks", myDocument);
            return Responder.makeMsgResponse("Added Track to Favorites");
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
