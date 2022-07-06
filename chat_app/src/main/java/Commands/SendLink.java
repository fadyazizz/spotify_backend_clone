package Commands;

import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.FireStoreInstance;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;



    public class SendLink extends ChatAppCommand{
        @NotBlank(message="link shouldnt be empty")
        String link;
        @NotBlank(message="room ID shouldnt be empty")
        String roomID;


        @Override
        public String getCommandName() {
            return "SendLink";
        }

        @Override
        public String execute()  {
            int id=Integer.parseInt(this.tokenPayload.getString("id"));


            Date date = new Date();
            HashMap<String, Object> data = new HashMap<>();
            Timestamp timestamp2 = new Timestamp(date.getTime());
            String format = "HEYYYY check out this amazing Media Link " + link +" Exclusively on Spotify";
            data.put("message", format);
            data.put("date",timestamp2.toString());
            data.put("room", this.roomID);
            data.put("user" , id);
            String uniqueID = UUID.randomUUID().toString();

            try {
                FireStoreInstance.addDocument("MessagesNew" , uniqueID+ timestamp2.toString(),data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Responder.makeMsgResponse("Link Shared Successfully");
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
                this.link = body.getString("link");
                this.roomID = body.getString("roomID");

            }
            catch (Exception e){
                throw new ValidationException("attributes data types are wrong");
            }
            this.validateAnnotations();
        }


}
