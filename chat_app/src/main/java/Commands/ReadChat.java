package Commands;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.FireStoreInstance;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ReadChat extends  ChatAppCommand{

    ArrayList<MessageRecord> messages = new ArrayList<>();
    @NotBlank(message="room ID shouldnt be empty")
    String roomID;


    @Override
    public String getCommandName() {
        return "ReadChat";
    }

    @Override
    public String execute()  {
        System.out.println("here is the past chat history in this server : ");
        ArrayList<QueryDocumentSnapshot> arr = null;
        try {
            arr = FireStoreInstance.retrieveAllDocuments("MessagesNew");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<HashMap<String,Object>> records = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            QueryDocumentSnapshot document = arr.get(i);
            if((document.get("room")+"").equals(this.roomID+""))
            {
                records.add((HashMap<String, Object>) document.getData());
            }
        }


        for (int i = 0; i <records.size() ; i++) {

            String message = (String) records.get(i).get("message");
            String date = (String) records.get(i).get("date");
            long user = (long) records.get(i).get("user");
             String userstring = user+"";
            MessageRecord messagerec = new MessageRecord(message , date , userstring);
            this.messages.add(messagerec);

        }
        Collections.sort(messages, (o1, o2) -> o1.date.compareTo(o2.date));
        String output = "";
        for (int i = 0; i < messages.size(); i++) {
             output+= "[" + messages.get(i).date + "] " + messages.get(i).user + " : " + messages.get(i).message
                     + " . "  +System.lineSeparator();
        }
        return Responder.makeMsgResponse(output);
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
            this.roomID = body.getString("roomID");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();
    }


}
