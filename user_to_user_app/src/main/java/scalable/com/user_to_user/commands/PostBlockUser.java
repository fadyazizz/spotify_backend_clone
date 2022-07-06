package scalable.com.user_to_user.commands;

import javax.validation.constraints.NotNull;

import com.arangodb.entity.BaseDocument;

import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;


public class PostBlockUser extends UserToUserCommand{

    @NotNull(message="blocked user username should not be empty")
    private int blocked_id;


    @Override
    public String execute() {

        int id=Integer.parseInt(this.tokenPayload.getString("id"));
//        int id = 1;
        Arango arango = null;
        try {
            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("user_to_user","blocked_ids",false);
            BaseDocument dbBlock = new BaseDocument();
            dbBlock.addAttribute("user_id",id);
            dbBlock.addAttribute("blocked_id", this.blocked_id);
            String key = id + "-" + blocked_id;
            dbBlock.setKey(key);
            // if i follow the user, block and remove from followers:
            boolean isFollowed = arango.documentExists("user_to_user","followed_ids", dbBlock.getKey());
            if(isFollowed){
                boolean deleted = arango.deleteDocument("user_to_user","followed_ids", dbBlock.getKey());
            }
            BaseDocument res = arango.createDocument("user_to_user","blocked_ids", dbBlock);
            System.out.println("Block "+res+" added");
            return Responder.makeMsgResponse("Blocked: "+blocked_id);
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
        try {
            this.blocked_id = body.getInt("blocked_id");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();
    }


    @Override
    public String getCommandName() {
        return "PostBlockUser";
    }

}
