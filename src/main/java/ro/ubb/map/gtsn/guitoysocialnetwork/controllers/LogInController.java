package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.Encrypt;

import java.io.IOException;

public class LogInController {
    SuperService superService;
    Stage loginStage;
    RegisterController registerController;
    double x,y;

    public SuperService getSuperService() {
        return superService;
    }

    public void setSuperService(SuperService superService) {
        logo.setEffect(shadow);
        titleBar.setEffect(shadow);
        this.superService = superService;
    }

    public Stage getLoginStage() {
        return loginStage;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
        try {
            loadRegisterStage();
        }
        catch (IOException ioException){ioException.printStackTrace();}
    }

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button logInButton;

    @FXML
    private Label showLabel;

    @FXML
    private AnchorPane window;

    @FXML
    public ImageView logo;
    DropShadow shadow = new DropShadow();

    @FXML
    public HBox titleBar;


    private void startMainStage(UserDTO loggedUser) throws IOException {
        Stage main = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/MainView.fxml"));

        BorderPane pane = loader.load();
        main.setScene(new Scene(pane));
        main.setTitle("Main Menu");

        MainController mainController = loader.getController();
        mainController.setSuperService(superService);
        mainController.setLoggedUser(loggedUser);
        mainController.setMainStage(main);
        mainController.setLoginStage(loginStage);

        mainController.init();

        main.show();
    }

    private void loadRegisterStage() throws IOException{
        Stage registerStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/RegisterView.fxml"));

        AnchorPane pane = loader.load();
        registerStage.setScene(new Scene(pane));

        registerController = loader.getController();
        registerController.setSuperService(superService);
        registerController.setRegisterStage(registerStage);
        registerController.setLoginStage(loginStage);
        registerController.setLogInController(this);
        registerController.init();
        registerStage.initStyle(StageStyle.TRANSPARENT);
    }

    @FXML
    protected void onClickLogInButton() throws IOException {
        String user = username.getText();
        String passHash = Encrypt.getSha(password.getText());

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user);
        userDTO.setPasswordHash(passHash);

        if(superService.checkPassword(userDTO)) {
            showLabel.setText("");
            UserDTO loggedUser = superService.getUser(userDTO);
            startMainStage(loggedUser);
            loginStage.hide();
        }
        else{
            showLabel.setText("Incorrect credentials");
        }
    }

    @FXML
    protected void onClickRegisterButton(){
        String userName = username.getText();
        String passWord = password.getText();
        registerController.setUsername(userName);
        registerController.setPassword(passWord);
        loginStage.hide();
        registerController.registerStage.show();
    }

    public void dragged(javafx.scene.input.MouseEvent mouseEvent) {
        loginStage.setX(mouseEvent.getScreenX()-x);
        loginStage.setY(mouseEvent.getScreenY()-y);
    }

    public void pressed(javafx.scene.input.MouseEvent mouseEvent) {
        x= mouseEvent.getSceneX();
        y= mouseEvent.getSceneY();
    }

    public void clickMinimize(MouseEvent mouseEvent) {
        loginStage.setIconified(true);
    }

    public void clickExit(MouseEvent mouseEvent) {
        loginStage.close();
    }

    public void setLabel(String newText){
        showLabel.setText(newText);
    }
}
