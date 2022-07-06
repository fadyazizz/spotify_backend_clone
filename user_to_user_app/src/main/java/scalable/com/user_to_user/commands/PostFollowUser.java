package scalable.com.user_to_user.commands;

import javax.validation.constraints.NotNull;

import com.arangodb.entity.BaseDocument;

import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;


public class PostFollowUser extends UserToUserCommand{

    @NotNull(message="followed user id should not be empty")
    private int followed_id;


    @Override
    public String execute() {

        int id=Integer.parseInt(this.tokenPayload.getString("id"));
//        int id = 1;
        Arango arango = null;
        try {
//            System.out.println("in try follow");
            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("user_to_user","followed_ids",false);
            BaseDocument dbFollow = new BaseDocument();
            dbFollow.addAttribute("user_id",id);
            dbFollow.addAttribute("followed_id", this.followed_id);
            String key = id + "-" + followed_id;
            dbFollow.setKey(key);
            // before creating the doc, check if user already followed:
            // by setting the key, whenever same user try to follow same user, it violates unique key constraint
//            boolean isFollowed = arango.documentExists("user_to_user","followed_ids", dbFollow.getKey());
//            if(isFollowed){
//                System.out.println("Error already following");
//                return Responder.makeMsgResponse(" You are already following this user!");
//            }
            // check if blocked:
            boolean isBlocked = arango.documentExists("user_to_user","blocked_ids", dbFollow.getKey());
            if(isBlocked){
                return Responder.makeErrorResponse("You can not follow a user you blocked!", 404);
            }
            BaseDocument res = arango.createDocument("user_to_user","followed_ids", dbFollow);
            System.out.println("Follow "+res+" added");
            // sending the info of the followed user to the recommendations app:
            JSONObject body = new JSONObject();
            body.put("followed_user_id", followed_id);
            //TODO: send to reem
//            App.sendMessageToApp("Recommendations", this.origRequest, "POST", "CreateUserEdge", null, body);
            return Responder.makeMsgResponse("Following: "+followed_id);
        } catch (Exception e) {
            System.out.println("E7na fel error");
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
    }     // change back to true after testing


    @Override
    public void validateAttributeTypes() throws ValidationException {
        try {

            this.followed_id = body.getInt("followed_id");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();

    }


    @Override
    public String getCommandName() {
        return "PostFollowUser";
    }


}
