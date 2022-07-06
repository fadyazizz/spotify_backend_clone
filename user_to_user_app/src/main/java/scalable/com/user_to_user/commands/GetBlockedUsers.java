package scalable.com.user_to_user.commands;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import org.json.JSONArray;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

import java.util.ArrayList;

public class GetBlockedUsers extends UserToUserCommand{
    
    @Override
    public String getCommandName() {
        return "GetBlockedUsers";
    }

    @Override
    public String execute() {
        Arango arango = null;
        JSONArray response = new JSONArray();
        try {
            int userId = Integer.parseInt(this.tokenPayload.getString("id"));
            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("user_to_user", "blocked_ids", false);
            ArangoCursor<BaseDocument> cursor = arango.filterCollection("user_to_user", "blocked_ids", "user_id", userId);
            ArrayList<String> attributes = new ArrayList<>();
            attributes.add("blocked_id");
            response = arango.parseOutput(cursor, "doc_id", attributes);
            return Responder.makeDataResponse(response).toString();
        } catch (Exception e) {
            return Responder.makeErrorResponse(e.getMessage(), 404).toString();
        }
    }

    @Override
    public String getRestAPIMethod() {
        return "GET";
    }

    @Override
    public boolean isAuthNeeded() {
        return true;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {
    }
}
