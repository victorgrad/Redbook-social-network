package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.Encrypt;


public class RegisterController {
    SuperService superService;
    Stage registerStage,loginStage;
    LogInController logInController;
    DropShadow dropShadow;
    double x,y;

    @FXML
    public TextField usernameField;
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Label err;
    @FXML
    public HBox titleBar;

    public void init(){
        clearAll();
        dropShadow = new DropShadow();
        titleBar.setEffect(dropShadow);
    }

    public void setSuperService(SuperService superService){
        this.superService = superService;
    }

    public void setLogInController(LogInController logInController){
        this.logInController = logInController;
    }

    public void setRegisterStage(Stage stage){
        this.registerStage = stage;
    }

    public void setLoginStage(Stage stage){
        this.loginStage = stage;
    }

    public void setUsername(String username){
        usernameField.setText(username);
    }

    public void setPassword(String password){
        passwordField.setText(password);
    }

    private void clearAll(){
        usernameField.clear();
        firstNameField.clear();
        lastNameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    public void onRegisterButtonClick(){
        err.setText("");
        if(passwordField.getText().compareTo(confirmPasswordField.getText())!=0){
            err.setText("Password differs from Confirm Password");
        }
        else {
            try {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(usernameField.getText());
                userDTO.setFirstname(firstNameField.getText());
                userDTO.setLastname(lastNameField.getText());
                userDTO.setPasswordHash(Encrypt.getSha(passwordField.getText()));
                superService.addUser(userDTO);
                clearAll();
                logInController.setLabel("");
                registerStage.hide();
                loginStage.show();
            } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
                Alert alert = new Alert(Alert.AlertType.WARNING, err.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    public void onCancelButtonClick(){
        clearAll();
        registerStage.hide();
        loginStage.show();
    }

    public void dragged(javafx.scene.input.MouseEvent mouseEvent) {
        registerStage.setX(mouseEvent.getScreenX()-x);
        registerStage.setY(mouseEvent.getScreenY()-y);
    }

    public void pressed(javafx.scene.input.MouseEvent mouseEvent) {
        x= mouseEvent.getSceneX();
        y= mouseEvent.getSceneY();
    }

    public void clickMinimize(MouseEvent mouseEvent) {
        registerStage.setIconified(true);
    }

    public void clickExit(MouseEvent mouseEvent) {
        registerStage.close();
    }
}
