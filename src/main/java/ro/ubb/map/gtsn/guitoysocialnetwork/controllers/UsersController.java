package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.RequestDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.models.UserModel;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class UsersController implements Observer {
    public Button prevPage;
    public Button nextPage;
    public Label pageText;
    SuperService superService;
    String username;
    ObservableList<UserModel> users= FXCollections.observableArrayList();
    Stage usersStage;
    public TextField searchTextField;

    long currentPage = 0L;
    long pages = 0L;

    @FXML
    TableView<UserModel> tableView;

    @FXML
    TableColumn<UserModel,String> usernames;

    @FXML
    TableColumn<UserModel,String> firstNames;

    @FXML
    TableColumn<UserModel,String> lastNames;

    @FXML
    TableColumn<UserModel,String> statuses;

    @FXML
    TableColumn<UserModel, String> colButtons;

    @Override
    public void update() {
        refresh();
    }

    public SuperService getSuperService() {
        return superService;
    }

    public void setSuperService(SuperService superService) {
        this.superService = superService;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStage(Stage stage){
        usersStage = stage;
    }

    public void initialise(){
        usernames.setCellValueFactory(new PropertyValueFactory<>("Username"));
        firstNames.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
        lastNames.setCellValueFactory(new PropertyValueFactory<>("Lastname"));
        statuses.setCellValueFactory(new PropertyValueFactory<>("Status"));
        colButtons.setCellValueFactory(new PropertyValueFactory<>("Action"));
        statuses.setStyle("-fx-alignment: CENTER;");
        tableView.setItems(users);
        usersStage.initStyle(StageStyle.TRANSPARENT);
        pageText.setAlignment(Pos.CENTER);
    }

    public void refresh(){
        pages = superService.getUserPageNumber();
        if(currentPage > pages || currentPage < 0){
            pages = 0;
        }
        pageText.setText("Page " + (currentPage + 1) + " of " + (pages + 1));

        users.clear();
        UserModel userDTO = new UserModel();
        userDTO.setUsername(username);
        try {
            superService.getUserPageByName(searchTextField.getText(), (int) currentPage).forEach(user->{
                if(user.getUsername().compareTo(username)!=0){
                    UserModel userModel = new UserModel(user);
                    AtomicBoolean isSent = new AtomicBoolean(false);
                    AtomicBoolean isFriend = new AtomicBoolean(false);
                    superService.getUserFriendRequests(user).forEach(requestDTO -> {
                        if(requestDTO.getFrom().getUsername().compareTo(username)==0 && requestDTO.getStatus().equals(Constants.PENDINGREQUEST)){
                            isSent.set(true);
                        }
                    });
                    superService.getUserFriends(user).forEach(friendshipDTO -> {
                        if(friendshipDTO.getUser2().getUsername().compareTo(username)==0){
                            isFriend.set(true);
                        }
                    });
                    if(isFriend.get()){
                        ImageView imageView = new ImageView(new Image("file:images/friends.png"));
                        imageView.setFitHeight(15);
                        imageView.setFitWidth(15);
                        userModel.setStatus(imageView);
                    } else if(isSent.get()) {
                        ImageView imageView = new ImageView(new Image("file:images/sent.png"));
                        imageView.setFitHeight(15);
                        imageView.setFitWidth(15);
                        userModel.setStatus(imageView);

                        Button button = new Button("Remove request");
                        button.setPrefWidth(150);

                        button.setOnAction(event -> {
                            RequestDTO requestDTO = new RequestDTO();
                            UserDTO sender = new UserDTO();
                            sender.setUsername(username);
                            requestDTO.setFrom(sender);

                            UserDTO selectedUserDTO= new UserDTO();
                            selectedUserDTO.setUsername(userModel.getUsername());
                            requestDTO.setTo(selectedUserDTO);
                            try {
                                superService.removeFriendRequest(requestDTO);
                            }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
                                Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
                                alert.showAndWait();
                            }
                            refresh();
                        });

                        userModel.setAction(button);
                    } else {
                        Button button = new Button("Send request");
                        button.setPrefWidth(150);

                        button.setOnAction(event -> {
                            RequestDTO requestDTO = new RequestDTO();
                            UserDTO sender = new UserDTO();
                            sender.setUsername(username);
                            requestDTO.setFrom(sender);

                            UserDTO selectedUserDTO= new UserDTO();
                            selectedUserDTO.setUsername(userModel.getUsername());
                            requestDTO.setTo(selectedUserDTO);
                            try {
                                superService.addFriendRequest(requestDTO);
                            }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
                                Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
                                alert.showAndWait();
                            }
                            refresh();
                        });

                        userModel.setAction(button);
                    }
                    users.add(userModel);
                }
            });
        }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void OnNextPageButtonClick(ActionEvent actionEvent) {
        if(currentPage < pages)
            currentPage += 1;
        refresh();
    }

    public void onPreviousPageButtonClick(ActionEvent actionEvent) {
        if(currentPage > 0)
            currentPage -= 1;
        refresh();
    }
}
