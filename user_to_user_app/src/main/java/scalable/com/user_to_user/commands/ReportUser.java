package scalable.com.user_to_user.commands;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.arangodb.entity.BaseDocument;

import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

public class ReportUser extends UserToUserCommand{

    @NotNull(message="reported id should not be empty")
      private int reported_id;
    @NotBlank(message="report comment should not be empty")
      private String comment;

    @Override
    public String execute() {
        int id=Integer.parseInt(this.tokenPayload.getString("id"));
        Arango arango = null;
         try {
            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("user_to_user","reports",false);
            BaseDocument dbReport = new BaseDocument();
            dbReport.addAttribute("user_id",id);
            dbReport.addAttribute("reported_id", this.reported_id);
            dbReport.addAttribute("comment", this.comment);
            BaseDocument res = arango.createDocument("user_to_user","reports", dbReport);
            System.out.println("Report "+res+" added");
            return Responder.makeMsgResponse("Reported");
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

            this.reported_id = body.getInt("reported_id");
            this.comment = body.getString("comment");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();

    }

    @Override
    public String getCommandName() {
        return "ReportUser";
    }
    
}
