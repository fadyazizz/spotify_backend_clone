package Commands;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.FireStoreInstance;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;


public class CreateChatRoom extends ChatAppCommand{



    @NotBlank(message="room name shouldnt be empty")
     String roomName;



    @Override
    public String getCommandName() {

        return "CreateChatRoom";
    }

    @Override
    public String execute() {
        int id=Integer.parseInt(this.tokenPayload.getString("id"));

        HashMap<String, Object> data = new HashMap<>();
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        data.put("creationDate",timestamp2.toString() );
        data.put("roomName", this.roomName);
        data.put("user" , id);
        try {
            FireStoreInstance.addDocument("ChatRooms" , this.roomName+"-"+id , data);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return Responder.makeMsgResponse("successfully created room");
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
            this.roomName = body.getString("roomName");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();
    }
}
