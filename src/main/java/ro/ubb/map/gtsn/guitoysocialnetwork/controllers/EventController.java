package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.models.EventModel;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.io.IOException;

public class EventController implements Observer {
    public Button prevPageButton;
    public Label pageLabel;
    public Button nextPageButton;
    BorderPane view;
    Pane eventPane, eventDetailsPane, newEventPane;

    private String username;
    private SuperService superService;

    Stage eventDetailsStage;
    EventDetailsController eventDetailsController;

    Stage newEventStage;
    NewEventController newEventController;

    int currentPage = 0;
    int pages = 0;

    @FXML
    public TableView<EventModel> tableView;
    @FXML
    public TableColumn<EventModel, String> names;
    @FXML
    public TableColumn<EventModel, String> dates;
    @FXML
    public TableColumn<EventModel, String> locations;
    @FXML
    public TableColumn<EventModel, String> descriptions;
    @FXML
    public TableColumn<EventModel,String> statuses;
    @FXML
    Button newButton;

    ObservableList<EventModel> events = FXCollections.observableArrayList();


    public void setEventPane(Pane eventPane){
        this.eventPane = eventPane;
    }

    @Override
    public void update() {
        refresh();
    }

    public void refresh(){
        pages = (int) superService.getExistingEventsPageNumber();
        if(currentPage > pages || currentPage < 0){
            pages = 0;
        }
        pageLabel.setText("Page " + (currentPage + 1) + " of " + (pages + 1));

        events.clear();
        try {
            superService.getExistingEventsPage(currentPage).forEach(event -> {
                EventModel eventModel = new EventModel(event);
                if (superService.isUserSubscribedTo(username, event.getId())) {
                    ImageView imageView = new ImageView(new Image("file:images/check.png"));
                    imageView.setFitHeight(15);
                    imageView.setFitWidth(15);
                    eventModel.setStatus(imageView);
                }
                events.add(eventModel);
            });
        }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void initialise() throws IOException {
        tableView.setRowFactory(tv -> {
            TableRow<EventModel> row = new TableRow<>();

            row.setOnMouseClicked(ev -> {
                if(!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1){
                    openEvent(row.getItem());
                }
            });

            return row;
        });


        names.setCellValueFactory(new PropertyValueFactory<>("Name"));
        dates.setCellValueFactory(new PropertyValueFactory<>("Date"));
        descriptions.setCellValueFactory(new PropertyValueFactory<>("Description"));
        locations.setCellValueFactory(new PropertyValueFactory<>("Location"));
        statuses.setCellValueFactory(new PropertyValueFactory<>("Status"));

        descriptions.setCellFactory(column -> {
            return new TableCell<EventModel, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        String[] split = item.split("\n");
                        String firstLine = split[0];
                        if (firstLine.length() > Constants.PREVIEW_LENGTH) {
                            firstLine = firstLine.substring(0, Constants.PREVIEW_LENGTH - 3);
                            firstLine += "...";
                        }

                        if(firstLine.length() < Constants.PREVIEW_LENGTH && split.length > 1){
                            firstLine += "...";
                        }
                        Text text = new Text(firstLine);
                        text.getStyleClass().add("texting");

                        setGraphic(text);
                    }
                }
            };
        });

        tableView.setItems(events);

        initEventDetails();
        initNewEvent();
    }

    private void initNewEvent() throws IOException {
        newEventStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/NewEventView.fxml"));

        newEventPane = loader.load();
        newEventStage.setScene(new Scene(newEventPane));

        newEventController = (NewEventController) loader.getController();
        newEventController.setSuperService(superService);
        newEventController.setBorderPane(view);
        newEventController.setPrevious(eventPane);
        newEventController.initialise();
        newEventController.refresh();
    }

    private void initEventDetails() throws IOException {
        eventDetailsStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/EventDetailsView.fxml"));

        eventDetailsPane = loader.load();
        eventDetailsStage.setScene(new Scene(eventDetailsPane));

        eventDetailsController = (EventDetailsController) loader.getController();
        eventDetailsController.setSuperService(superService);
        eventDetailsController.setUsername(username);
        eventDetailsController.setBorderPane(view);
        eventDetailsController.setPrevious(eventPane);
        eventDetailsController.initialise();
        eventDetailsController.refresh();

        pageLabel.setAlignment(Pos.CENTER);
    }

    private void openEvent(EventModel item) {
        eventDetailsController.setEventID(item.getId());
        eventDetailsController.refresh();
        view.setCenter(eventDetailsPane);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SuperService getSuperService() {
        return superService;
    }

    public void setSuperService(SuperService superService) {
        this.superService = superService;
    }

    public void setBorderPane(BorderPane borderPane){
        this.view = borderPane;
    }

    @FXML
    public void onNewButtonClick(){
        view.setCenter(newEventPane);
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
