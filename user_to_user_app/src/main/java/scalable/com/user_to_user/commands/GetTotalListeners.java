package scalable.com.user_to_user.commands;

import com.beust.jcommander.Parameter;
import org.hibernate.validator.constraints.URL;
import org.jboss.logging.annotations.Param;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.music.MusicApp;
import scalable.com.shared.App;
import scalable.com.shared.classes.Responder;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.user_to_user.UserToUserApp;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;

public class GetTotalListeners extends UserToUserCommand{

    @NotBlank(message="Artist name should not be empty")
    private String artist;

    @Override
    public String getCommandName() {
        return "GetTotalListeners";
    }

    @Override
    public String execute() {
        //JSONObject communicateWithApp = new JSONObject(this.origRequest.toString());
        //communicateWithApp.put("commandName", "Test");
        //System.out.println(communicateWithApp);
        origRequest.put("methodType", "GET");
        System.out.println(origRequest.toString());

        //UserToUserApp interactionsApp = new UserToUserApp();
        //MusicApp streamMusicApp = new MusicApp();
        System.out.println("here 1");
        JSONObject communicateWithApp = App.communicateWithApp("Music", "Music",
                origRequest, "GET", "Test", null, body);
        System.out.println("here 2" + (communicateWithApp == null));
        return Responder.makeMsgResponse("Response from message queue : " + communicateWithApp.get("msg"));
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
            this.artist = body.getString("artist");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(this);
        if(!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(cv -> cv.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ValidationException(errorMessage);
        }
    }
}
