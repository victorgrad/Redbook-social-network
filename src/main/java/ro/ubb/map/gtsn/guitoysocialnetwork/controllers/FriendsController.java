package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.FriendshipDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;


public class FriendsController implements Observer {
    public Button prevPage;
    public Button nextPage;
    public Label pageLabel;
    SuperService superService;
    String username;
    Stage friendsStage;
    ObservableList<UserDTO> friends= FXCollections.observableArrayList();

    int currentPage, pages;

    @FXML
    TableView<UserDTO> tableView;

    @FXML
    TableColumn<UserDTO,String> usernames;

    @FXML
    TableColumn<UserDTO,String> firstNames;

    @FXML
    TableColumn<UserDTO,String> lastNames;

    @FXML
    TableColumn<UserDTO, Void> colButtons;

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

    public void setStage(Stage stage){
        friendsStage=stage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void initialise(){
        usernames.setCellValueFactory(new PropertyValueFactory<>("Username"));
        firstNames.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
        lastNames.setCellValueFactory(new PropertyValueFactory<>("Lastname"));
        tableView.setItems(friends);
        friendsStage.initStyle(StageStyle.TRANSPARENT);

        colButtons.setCellFactory(new Callback<TableColumn<UserDTO, Void>, TableCell<UserDTO, Void>>() {
            @Override
            public TableCell<UserDTO, Void> call(TableColumn<UserDTO, Void> param) {
               TableCell<UserDTO, Void> cell = new TableCell<>() {
                   final Button button = new Button("Remove");
                   {
                       button.setOnAction(event -> {
                           UserDTO userDTO = param.getTableView().getItems().get(this.getIndex());
                           FriendshipDTO friendshipDTO = new FriendshipDTO();
                           UserDTO userDTO1 = new UserDTO();
                           userDTO1.setUsername(username);
                           friendshipDTO.setUser1(userDTO1);
                           friendshipDTO.setUser2(userDTO);
                           try {
                               superService.removeFriendship(friendshipDTO);
                           } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
                               Alert alert = new Alert(Alert.AlertType.WARNING, err.getMessage(), ButtonType.OK);
                               alert.showAndWait();
                           }
                           refresh();
                       });
                   }

                   @Override
                   public void updateItem(Void item, boolean empty){
                       super.updateItem(item, empty);
                       if(empty){
                           setGraphic(null);
                       } else{
                            setGraphic(button);
                       }
                   }

               };
               cell.setAlignment(Pos.CENTER);
               return cell;
            }
        });
        pageLabel.setAlignment(Pos.CENTER);
    }

    public void refresh(){
        pages = (int) superService.getFriendshipPageNumber(username);
        if(currentPage > pages || currentPage < 0){
            pages = 0;
        }
        pageLabel.setText("Page " + (currentPage + 1) + " of " + (pages + 1));

        friends.clear();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        try {
            Iterable<FriendshipDTO> friendshipDTOS = superService.getUserFriendsPage(userDTO, currentPage);
            for(FriendshipDTO f: friendshipDTOS){
                friends.add(f.getUser2());
            }
        }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void onNextPageButtonClick(ActionEvent actionEvent) {
        if(currentPage < pages)
            currentPage += 1;
        refresh();
    }

    public void onPrevPageButtonClick(ActionEvent actionEvent) {
        if(currentPage > 0)
            currentPage -= 1;
        refresh();
    }
}
