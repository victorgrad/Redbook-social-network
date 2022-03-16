package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.EventDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

public class EventDetailsController {
    String username;
    Long eventID;
    SuperService superService;

    Pane previous;
    BorderPane view;

    @FXML
    TextArea textArea;
    @FXML
    CheckBox checkNotification;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public void setSuperService(SuperService superService) {
        this.superService = superService;
    }

    public void setPrevious(Pane previous) {
        this.previous = previous;
    }

    public void setBorderPane(BorderPane view) {
        this.view = view;
    }

    public void initialise(){
        //todo
    }

    public void refresh(){
        if(eventID!=null) {
            EventDTO event = superService.getEvent(eventID);
            textArea.setText(eventToString(event));
            checkNotification.setSelected(false);
        }
    }

    private String eventToString(EventDTO event){
        StringBuilder text = new StringBuilder();
        text.append("Name: ").append(event.getName()).append("\n");
        text.append("Date: ").append(event.getDateTime().format(Constants.DATE_TIME_FORMATTER2)).append("\n");
        text.append("Location: ").append(event.getLocation()).append("\n");
        text.append("Description: \n").append(event.getDescription()).append("\n");

        return text.toString();
    }

    @FXML
    public void onBackButtonClick(){
        view.setCenter(previous);
    }

    @FXML
    public void onSubscribeButtonClick(){
        try {
            superService.addEventParticipant(eventID,username,checkNotification.isSelected());
            onBackButtonClick();
        }catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }
}
