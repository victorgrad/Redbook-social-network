package ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Set;

public class MessageValidator implements Validator<Message>{
    /**
     * Validates the Message Object
     * @param entity Message, the object to be validated
     * @throws ValidationException
     */
    @Override
    public void validate(Message entity) throws ValidationException {
        String errors = "";
        String from = entity.getFrom();
        Set<String> to = entity.getTo();
        String message = entity.getMessage();
        LocalDateTime messageDateTime = entity.getMessageDateTime();
        boolean toSetCheck=true;
        boolean toSetEmpty=false;

        if(from == null){
            errors += "message sender must not be null\n";
        }
        if(to == null){
            errors += "to set must not be null\n";
        }
        else {
            for (String user : to) {
                if (user == null) {
                    toSetCheck = false;
                    break;
                }
            }
        }
        if(!toSetCheck){
            errors += "one or more receivers must not be null\n";
        }
        if(messageDateTime == null){
            errors += "date and time must not be null\n";
        }
        if(message == null){
            errors += "message must not be null\n";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }

        if(from.length() > Constants.MAXLENGTH){
            errors += "message sender username must not exceed 64 characters in length\n";
        }
        for(String user:to){
            if(user.length() > Constants.MAXLENGTH){
                toSetCheck=false;
            }
            if(user.isEmpty()){
                toSetEmpty=true;
            }
        }
        if(!toSetCheck){
            errors += "one or more receivers usernames must not exceed 64 characters in length\n";
        }
        if(from.isEmpty()){
            errors += "message sender username must not be empty\n";
        }
        if(toSetEmpty){
            errors += "one or more receivers usernames must not be empty\n";
        }
        if(message.isEmpty()){
            errors += "message must not be empty\n";
        }
        if(to.isEmpty()){
            errors += "message must have at least one receiver\n";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
