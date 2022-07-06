package Commands;

import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.FireStoreInstance;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class SendMessage extends ChatAppCommand{
    @NotBlank(message="message shouldnt be empty")
    String message;
    @NotBlank(message="room ID shouldnt be empty")
    String roomID;


    @Override
    public String getCommandName() {
        return "SendMessage";
    }

    @Override
    public String execute()  {
        int id=Integer.parseInt(this.tokenPayload.getString("id"));


        Date date = new Date();
        HashMap<String, Object> data = new HashMap<>();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        data.put("message", this.message);
        data.put("date",timestamp2.toString());
        data.put("room", this.roomID);
        data.put("user" , id);
        String uniqueID = UUID.randomUUID().toString();

        try {
            FireStoreInstance.addDocument("MessagesNew" , uniqueID+ timestamp2.toString(),data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Responder.makeMsgResponse("Message Sent Successfully");
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
            this.message = body.getString("message");
            this.roomID = body.getString("roomID");

        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();
    }
}
