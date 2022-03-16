package ro.ubb.map.gtsn.guitoysocialnetwork.domain.models;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;

public class UserModel {
    private String username;
    private String firstname;
    private String lastname;
    private ImageView status;
    private Button action;


    public UserModel(String username, String firstname, String lastname,ImageView status){
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
    }

    public UserModel(User user){
        username = user.getId();
        firstname = user.getFirstName();
        lastname = user.getLastName();
        status=null;
    }

    public UserModel(UserDTO userDTO){
        username = userDTO.getUsername();
        firstname = userDTO.getFirstname();
        lastname = userDTO.getLastname();
        status=null;
    }

    public Button getAction() {
        return action;
    }

    public void setAction(Button button) {
        this.action = button;
    }

    /**
     * Class constructor without parameters (!!!Must use set methods)
     */
    public UserModel(){
        username = null;
        firstname = null;
        lastname = null;
        status = null;
    }

    /**
     * Returns the id of the user
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the firstname of the user
     * @return String
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Returns the lastname of the user
     * @return String
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the username of the user
     * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the first name of the user
     * @param firstname String
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Sets the last name of the user
     * @param lastname String
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ImageView getStatus() {
        return status;
    }

    public void setStatus(ImageView status) {
        this.status = status;
    }
}
