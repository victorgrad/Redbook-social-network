package ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

public class UserValidator implements Validator<User>{
    /**
     * Checks if the text contains only alphanumeric characters
     * @param text String the string to be verified
     * @return true if the string only contains alphanumeric characters, false otherwise
     */
    private boolean isAlphaNumeric(String text){
        return text != null && text.matches("^[a-zA-Z0-9]*$");
    }

    /**
     * Validates the User object
     * @param entity User, the object to be validated
     * @throws ValidationException if the object is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        String userID = entity.getId();
        String firstname = entity.getFirstName();
        String lastname = entity.getLastName();

        if(userID == null){
            errors += "username must not be null\n";
        }
        if(firstname == null){
            errors += "firstname must not be null\n";
        }
        if(lastname == null){
            errors += "lastname must not be null\n";
        }
        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }

        if(userID.isEmpty()){
            errors += "username must not be empty\n";
        }
        if(firstname.isEmpty()){
            errors += "firstname must not be empty\n";
        }
        if(lastname.isEmpty()){
            errors += "lastname must not be empty\n";
        }

        if(userID.length() > Constants.MAXLENGTH){
            errors += "username must not exceed 64 characters in length\n";
        }
        if(firstname.length() > Constants.MAXLENGTH){
            errors += "firstname must not exceed 64 characters in length\n";
        }
        if(lastname.length() > Constants.MAXLENGTH){
            errors += "lastname must not exceed 64 characters in length\n";
        }

        if(!isAlphaNumeric(userID)){
            errors += "username must contain only letter or numbers\n";
        }

        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
