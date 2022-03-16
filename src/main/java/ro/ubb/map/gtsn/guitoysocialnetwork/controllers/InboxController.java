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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.models.MessageModel;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class InboxController implements Observer {
    public TableView<MessageModel> tableView;
    public TableColumn<MessageModel, String> fromUsernames;
    public TableColumn<MessageModel, String> recipients;
    public TableColumn<MessageModel, String> dates;
    public TableColumn<MessageModel, String> messages;
    public Button prevPage;
    public Button nextPage;
    public Label pageLabel;
    BorderPane view;


    int currentPage, pages;
    ObservableList<MessageModel> threads = FXCollections.observableArrayList();


    private String username;
    private SuperService superService;

    Stage inboxStage;
    Pane inboxPane;

    MessageController messageController;
    Pane messagePane;
    Stage messageStage;

    ComposeMessageController composeMessageController;
    Pane composeMessagePane;
    Stage composeMessageStage;

    public void setInboxStage(Stage inboxStage) {
        this.inboxStage = inboxStage;
    }

    public void setInboxPane(Pane inboxPane) {
        this.inboxPane = inboxPane;
    }

    @Override
    public void update() {
        refresh();
    }

    public void refresh() {
        pages = (int) superService.getMessagePageNumber(username);
        if(currentPage > pages || currentPage < 0){
            pages = 0;
        }
        pageLabel.setText("Page " + (currentPage + 1) + " of " + (pages + 1));
        threads.clear();

        if (username == null) {
            return;
        }


        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);

            Set<Message> messages = superService.getLastUserMessagesFromChainPage(userDTO, currentPage);
            messages.forEach(message -> threads.add(new MessageModel(message)));

        } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
            Alert alert = new Alert(Alert.AlertType.WARNING, err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void initialise() throws IOException {
        tableView.setRowFactory(tv -> {
            TableRow<MessageModel> row = new TableRow<>();

            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1) {
                    openMessage(row.getItem());
                }
            });

            return row;
        });

        inboxStage.setOnHidden(ev -> messageStage.hide());

        fromUsernames.setCellValueFactory(new PropertyValueFactory<>("From"));
        recipients.setCellValueFactory(new PropertyValueFactory<>("Recipients"));
        dates.setCellValueFactory(new PropertyValueFactory<>("Date"));
        messages.setCellValueFactory(new PropertyValueFactory<>("Message"));

        messages.setCellFactory(column -> {
            return new TableCell<MessageModel, String>() {
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

                        if (firstLine.length() < Constants.PREVIEW_LENGTH && split.length > 1) {
                            firstLine += "...";
                        }
                        Text text = new Text(firstLine);
                        text.getStyleClass().add("texting");

                        setGraphic(text);

                    }
                }
            };
        });

        tableView.setItems(threads);
        inboxStage.initStyle(StageStyle.TRANSPARENT);
        pageLabel.setAlignment(Pos.CENTER);
        initMessageStage();
        initComposeMessageStage();
    }

    private void initMessageStage() throws IOException {
        messageStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/MessageView.fxml"));

        messagePane = loader.load();
        messageStage.setScene(new Scene(messagePane));
        messageStage.setTitle("Message details");

        messageController = (MessageController) loader.getController();
        messageController.setSuperService(superService);
        messageController.setUsername(username);
        messageController.setStage(messageStage);
        messageController.setBorderPane(view);
        messageController.setPrevious(inboxPane);
        messageController.initialise();
        messageController.refresh();

        superService.addObserver(messageController);
    }

    private void initComposeMessageStage() throws IOException {
        composeMessageStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/ComposeMessageView.fxml"));

        composeMessagePane = loader.load();
        composeMessageStage.setScene(new Scene(composeMessagePane));

        composeMessageController = (ComposeMessageController) loader.getController();
        composeMessageController.setSuperService(superService);
        composeMessageController.setUsername(username);
        composeMessageController.setStage(composeMessageStage);
        composeMessageController.setBorderPane(view);
        composeMessageController.setPrevious(inboxPane);

        composeMessageController.initialise();
        composeMessageController.refresh();

        superService.addObserver(composeMessageController);
    }

    private void openMessage(MessageModel item) {
        messageController.setMessageID(item.getMessageId());
        messageController.refresh();
        view.setCenter(messagePane);
    }

    public void onComposeMessageButtonClick() {
        composeMessageController.refresh();
        view.setCenter(composeMessagePane);
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

    public void setBorderPane(BorderPane borderPane) {
        this.view = borderPane;
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
