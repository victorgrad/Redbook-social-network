package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.RequestDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.models.RequestModel;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.function.Predicate;

public class RequestController implements Observer {
    public Button prevPage;
    public Button nextPage;
    public Label pageLabel;
    SuperService superService;
    String username;
    ObservableList<RequestModel> requests = FXCollections.observableArrayList();
    Stage requestStage;

    int currentPage = 0;
    int pages = 0;

    @FXML
    TableView<RequestModel> tableView;

    @FXML
    TableColumn<RequestModel,String> usernames;

    @FXML
    TableColumn<RequestModel,String> firstNames;

    @FXML
    TableColumn<RequestModel,String> lastNames;

    @FXML
    TableColumn<RequestModel,String> statuses;

    @FXML
    TableColumn<RequestModel,String> dates;

    @FXML
    TableColumn<RequestModel, Void> colButtons;

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
        requestStage=stage;
    }

    public void initialise(){
        usernames.setCellValueFactory(new PropertyValueFactory<>("Username"));
        firstNames.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        lastNames.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        statuses.setCellValueFactory(new PropertyValueFactory<>("Status"));
        dates.setCellValueFactory(new PropertyValueFactory<>("Date"));
        tableView.setItems(requests);
        requestStage.initStyle(StageStyle.TRANSPARENT);

        colButtons.setCellFactory(new Callback<TableColumn<RequestModel, Void>, TableCell<RequestModel, Void>>() {
            @Override
            public TableCell<RequestModel, Void> call(TableColumn<RequestModel, Void> param) {
                TableCell<RequestModel, Void> cell = new TableCell<>() {
                    final Button acceptButton = new Button("Accept");
                    {
                        acceptButton.setPrefWidth(70);
                        acceptButton.setOnAction(event -> {
                            RequestModel requestModel = param.getTableView().getItems().get(this.getIndex());
                            RequestDTO requestDTO = prepareRequestDTO(requestModel);
                            try {
                                superService.acceptFriendRequest(requestDTO);
                            }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
                                Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
                                alert.showAndWait();
                            }
                            refresh();
                        });
                    }

                    final Button rejectButton = new Button("Reject");
                    {
                        rejectButton.setPrefWidth(70);
                        rejectButton.setOnAction(event -> {
                            RequestModel requestModel = param.getTableView().getItems().get(this.getIndex());
                            RequestDTO requestDTO = prepareRequestDTO(requestModel);
                            try {
                                superService.rejectFriendRequest(requestDTO);
                            }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
                                Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
                                alert.showAndWait();
                            }
                            refresh();
                        });
                    }

                    final HBox hBox = new HBox();
                    {
                        hBox.getChildren().add(acceptButton);
                        hBox.getChildren().add(rejectButton);

                        hBox.setSpacing(1);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty){
                        super.updateItem(item, empty);
                        if(empty){
                            setGraphic(null);
                        } else{
                            setGraphic(hBox);
                        }
                    }

                };
                return cell;
            }
        });
        pageLabel.setAlignment(Pos.CENTER);
    }

    public void refresh(){
        pages = (int) superService.getRequestPageNumber(username);
        if(currentPage > pages || currentPage < 0){
            pages = 0;
        }
        pageLabel.setText("Page " + (currentPage + 1) + " of " + (pages + 1));

        requests.clear();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        Predicate<RequestDTO> predicate = requestDTO -> requestDTO.getStatus().equals(Constants.PENDINGREQUEST);

        try {
            List<RequestDTO> requestDTOS = superService.getUserFriendRequestsPage(userDTO, currentPage);

            requestDTOS.stream().filter(predicate).forEach(requestDTO -> {
                RequestModel requestModel = new RequestModel(requestDTO);
                requests.add(requestModel);
            });
        }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private RequestDTO prepareRequestDTO(RequestModel selectedRequest) {
        RequestDTO requestDTO = new RequestDTO();
        UserDTO sender = new UserDTO();
        UserDTO receiver = new UserDTO();
        receiver.setUsername(username);
        sender.setUsername(selectedRequest.getUsername());
        requestDTO.setFrom(sender);
        requestDTO.setTo(receiver);

        return requestDTO;
    }

    public void onNextPageButtonClick(ActionEvent actionEvent) {
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
