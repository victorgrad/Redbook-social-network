package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.EventDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NewEventController {
    SuperService superService;
    Pane previous;
    BorderPane view;
    int initValue = 0;


    @FXML
    TextField nameField;
    @FXML
    DatePicker datePicker;
    @FXML
    TextArea descriptionArea;
    @FXML
    TextField locationField;
    @FXML
    Spinner<Integer> hour;
    @FXML
    Spinner<Integer> minute;


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
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,initValue);
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,initValue);
        hour.setValueFactory(hourFactory);
        minute.setValueFactory(minuteFactory);
    }

    public void refresh(){
        nameField.clear();
        datePicker.setValue(LocalDate.now());
        hour.getValueFactory().setValue(0);
        minute.getValueFactory().setValue(0);
        descriptionArea.clear();
        locationField.clear();
    }

    @FXML
    public void onBackButtonClick(){
        view.setCenter(previous);
        refresh();
    }

    @FXML
    public void onConfirmEventButtonClick(){
        String eventName = nameField.getText();
        LocalDate eventDate = datePicker.getValue();
        Integer eventHour = hour.getValue();
        Integer eventMinute = minute.getValue();
        String eventDescription = descriptionArea.getText();
        String eventLocation = locationField.getText();
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, LocalTime.of(eventHour,eventMinute));

        try{
            EventDTO newEvent = new EventDTO(eventName,eventDateTime,eventDescription,eventLocation);
            superService.addEvent(newEvent);
            onBackButtonClick();

        } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }
}
