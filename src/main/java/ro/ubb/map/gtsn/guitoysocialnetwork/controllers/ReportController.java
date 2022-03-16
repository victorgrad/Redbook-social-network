package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class ReportController implements Observer {
    public DatePicker beginDatePicker;
    public DatePicker endDatePicker;
    public TextField textField;
    StringConverter<UserDTO> userConverter = new StringConverter<UserDTO>() {
        @Override
        public String toString(UserDTO object) {
            return object.getUsername();
        }

        @Override
        public UserDTO fromString(String string) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(string);
            return superService.getUser(userDTO);
        }
    };

    String username2;

    SuperService superService;
    String username;

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
    
    public void initialise(){
        TextFields.bindAutoCompletion(textField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<UserDTO>>(){

            @Override
            public Collection<UserDTO> call(AutoCompletionBinding.ISuggestionRequest param) {
                return superService.getUserByName(param.getUserText());
            }
        }, userConverter);
    }
    
    public void refresh(){
    }

    public void onActivityReportButtonClicked(ActionEvent actionEvent) {
        LocalDate beginDate = beginDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if(beginDate == null || endDate == null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Must select a begin and an end date", ButtonType.OK);
            alert.showAndWait();
        } else if(beginDate.isAfter(endDate)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Begin date must be before end date", ButtonType.OK);
            alert.showAndWait();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.setInitialFileName("UserActivityReport");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter( "PDF files", "*.pdf"));

            File selectedFile = fileChooser.showSaveDialog(beginDatePicker.getScene().getWindow());
            try {
                if(selectedFile != null)
                    superService.getReportService().generateActivityReport(username, selectedFile.getAbsolutePath(), beginDate, endDate);
            } catch (Exception err){
                Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    public void onMessageReportButtonClicked(ActionEvent actionEvent) {
        LocalDate beginDate = beginDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        username2 = textField.getText();

        if(beginDate == null || endDate == null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Must select a begin and an end date", ButtonType.OK);
            alert.showAndWait();
        } else if(beginDate.isAfter(endDate)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Begin date must be before end date", ButtonType.OK);
            alert.showAndWait();
        } else if(username2 == null || username2.equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Must select an valid username", ButtonType.OK);
            alert.showAndWait();
        } else {
            try {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(username2);
                superService.getUser(userDTO);

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save");
                fileChooser.setInitialFileName("UserMessageReport");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter( "PDF files", "*.pdf"));

                File selectedFile = fileChooser.showSaveDialog(beginDatePicker.getScene().getWindow());
                if(selectedFile != null)
                    superService.getReportService().generateMessageReport(username,  username2, selectedFile.getAbsolutePath(), beginDate, endDate);
            } catch (DuplicateException | NotFoundException | RepoException | ValidationException err){
                Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
                alert.showAndWait();
            } catch (IOException err){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid file or no permission to write", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    @Override
    public void update() {
        refresh();
    }
}
