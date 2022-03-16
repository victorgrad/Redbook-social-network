package ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Friendship;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

import java.time.LocalDate;

public class FriendshipValidator implements Validator<Friendship>{
    /**
     * Validates the friendship object
     * @param entity Friendship, the object to be validated
     * @throws ValidationException if the object is not valid
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";
        String userID1 = entity.getId().getLeft();
        String userID2 = entity.getId().getRight();
        LocalDate dateTime = entity.getFriendshipDate();

        if(userID1 == null){
            errors += "left username must not be null\n";
        }
        if(userID2 == null){
            errors += "right username must not be null\n";
        }
        if(dateTime == null){
            errors += "date must not be null\n";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }

        if(userID1.length() > Constants.MAXLENGTH){
            errors += "left username must not exceed 64 characters in length\n";
        }
        if(userID2.length() > Constants.MAXLENGTH){
            errors += "right username must not exceed 64 characters in length\n";
        }

        if(userID1.equals(userID2)){
            errors += "usernames must be distinct\n";
        }
        if(userID1.isEmpty()){
            errors += "left username must not be empty\n";
        }
        if(userID2.isEmpty()){
            errors += "right username must not be empty\n";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
