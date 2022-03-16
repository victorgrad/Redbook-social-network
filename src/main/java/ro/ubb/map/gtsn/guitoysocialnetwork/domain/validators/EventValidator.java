package ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Set;

public class EventValidator implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException {
        String errors = "";
        String name = entity.getName();
        String description = entity.getDescription();
        Set<String> usernames = entity.getParticipantUsernames();
        LocalDateTime dateTime = entity.getDateTime();


        if(description == null){
            errors += "description must not be null\n";
        }
        if(usernames == null){
            errors += "set must not be null\n";
        }
        if(name == null){
            errors += "name must not be null\n";
        }
        if(dateTime == null){
            errors += "date must not be null\n";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
        if(name.length() > Constants.MAXLENGTH){
            errors += "name must not exceed 64 characters in length\n";
        }
        if(name.isEmpty()){
            errors += "name must not be empty\n";
        }
        if(dateTime.isBefore(LocalDateTime.now())){
            errors += "cannot add event in a past time";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
