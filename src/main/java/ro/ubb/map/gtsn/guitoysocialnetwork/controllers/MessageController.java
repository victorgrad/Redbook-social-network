package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;
import java.util.List;

public class MessageController implements Observer {
    public ListView<String> messageListView;
    public Label pageLabel;
    Stage messageStage;
    public TextArea textArea;
    public Button replyAllButton;
    ObservableList<String> messages = FXCollections.observableArrayList();
    BorderPane view;
    Pane previous;

    SuperService superService;
    String username;
    Long messageID;

    int currentPage = 0;
    int pages = 0;

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
        messageStage=stage;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public void setBorderPane(BorderPane borderPane){
        this.view = borderPane;
    }

    public void setPrevious(Pane pane){
        this.previous=pane;
    }

    @Override
    public void update() {
        refresh();
    }

    private String messageToString(Message m){
        StringBuilder text = new StringBuilder();
        text.append("From: ").append(m.getFrom()).append("\n");
        text.append("To: ");
        for(String username: m.getTo()){
            text.append(username).append(", ");
        }
        text.append("\n");
        text.append("Date: ").append(m.getMessageDateTime().format(Constants.DATE_TIME_FORMATTER2)).append("\n");

        text.append(m.getMessage());

        return text.toString();
    }

    private void addMessages(List<Message> msgs){
        for(Message m: msgs){
            String text = messageToString(m);
            messages.add(text);
        }
    }

    private void addMessagesPage(List<Message> msgs, int currentPage){
        int start = msgs.size() - Constants.PAGE_SIZE * (currentPage + 1);
        int end = msgs.size() - Constants.PAGE_SIZE * currentPage;

        if(start < 0){
            start = 0;
        }

        if(end > msgs.size()){
            end = msgs.size();
        }

        for(int i = start; i < end; i++){
            String text = messageToString(msgs.get(i));
            messages.add(text);
        }
    }

    public void refresh() {
        if(messageID != null) {
            List<Message> msgThread = superService.getMessageReplyThread(messageID);
            messages.clear();

            pages = msgThread.size() / Constants.PAGE_SIZE;
            if(currentPage > pages || currentPage < 0){
                pages = 0;
            }
            pageLabel.setText("Page " + (currentPage + 1) + " of " + (pages + 1));

            addMessagesPage(msgThread, currentPage);
        }
    }

    private void addReply(Message newMessage) {
        String text = messageToString(newMessage);
        messages.add(text);
    }

    public void onReplyAllButtonClicked() {
        String text = textArea.getText();
        textArea.clear();

        try{
            Message newMessage = superService.replyAllMessage(username, text, messageID);
            messageID = newMessage.getId();
            refresh();

        } catch (DuplicateException | NotFoundException | RepoException | ValidationException err) {
            Alert alert = new Alert(Alert.AlertType.WARNING,err.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void onBackButtonClicked(){
        view.setCenter(previous);
    }

    public void initialise() {
        messageListView.setItems(messages);
        messageStage.initStyle(StageStyle.TRANSPARENT);
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
