package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.FriendshipDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComposeMessageController implements Observer {

    public TableView<UserDTO> tableView;
    public TextArea textArea;
    public TextField textField;
    private SuperService superService;
    private String username;
    private BorderPane view;
    private Pane previous;
    private Stage composeMessageStage;

    public Button addRecipientButton;
    public Button sendButton,  clearButton;

    public TableColumn<UserDTO, String> firstnames;
    public TableColumn<UserDTO, String> lastnames;
    public TableColumn<UserDTO, String> colButtons;
    private ObservableList<UserDTO> sendingTo = FXCollections.observableArrayList();

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

    public void setSuperService(SuperService superService) {
        this.superService = superService;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBorderPane(BorderPane view) {
        this.view = view;
    }

    public void setPrevious(Pane previous) {
        this.previous = previous;
    }

    public void setStage(Stage stage){
        this.composeMessageStage = stage;
    }

    public void initialise(){
        composeMessageStage.initStyle(StageStyle.TRANSPARENT);

        firstnames.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("Firstname"));
        lastnames.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("Lastname"));

        tableView.setItems(sendingTo);

        colButtons.setCellFactory(new Callback<TableColumn<UserDTO, String>, TableCell<UserDTO, String>>() {
            @Override
            public TableCell<UserDTO, String> call(TableColumn<UserDTO, String> param) {
                TableCell<UserDTO, String> cell = new TableCell<>() {
                    final Button button = new Button("X");
                    {
                        button.setOnAction(event -> {
                            sendingTo.remove(this.getIndex());
                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty){
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

        TextFields.bindAutoCompletion(textField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<UserDTO>>(){

            @Override
            public Collection<UserDTO> call(AutoCompletionBinding.ISuggestionRequest param) {
                return superService.getUserByName(param.getUserText());
            }
        }, userConverter);
    }

    public void refresh(){
    }

    @Override
    public void update() {
        refresh();
    }

    public void onAddRecipientButton() {
        String username2 = textField.getText();
        UserDTO added = null;
        try{
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username2);
            added = superService.getUser(userDTO);
        } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }

        if(added != null && !sendingTo.contains(added)){
            sendingTo.add(added);
        }
    }

    public void onSendButtonClicked() {
        String from = username;
        String text = textArea.getText();
        List<String> toList = new ArrayList<>();

        for(UserDTO userDTO: sendingTo){
            toList.add(userDTO.getUsername());
        }

        try{
            superService.sendMessage(from, text, toList);
            textArea.clear();
            sendingTo.clear();
        } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void onClearButtonClicked() {
        textArea.clear();
        sendingTo.clear();
    }

    public void onBackButtonClick() {
        view.setCenter(previous);
    }
}
