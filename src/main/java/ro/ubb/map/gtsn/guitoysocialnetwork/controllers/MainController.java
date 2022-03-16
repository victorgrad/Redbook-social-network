package ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Notification;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Page;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.io.IOException;

public class MainController implements Observer {
    public Label loggedAs;
    double x,y;
    SuperService superService;
    UserDTO loggedUser;
    Page userPage;

    Pane friendsPane, usersPane,requestPane,eventPane,reportPane,inboxPane;

    @FXML
    Button notificationButton;
    @FXML
    ListView<Notification> notificationListView;
    Boolean openNotifications;
    ObservableList<Notification> notifications = FXCollections.observableArrayList();

    Stage mainStage, loginStage;

    Stage friendsStage;
    FriendsController friendsController;

    Stage usersStage;
    UsersController usersController;

    Stage requestsStage;
    RequestController requestController;

    Stage inboxStage;
    InboxController inboxController;

    Stage eventStage;
    EventController eventController;

    Stage reportStage;
    ReportController reportController;


    @FXML
    Button showFriendsMenu;
    @FXML
    Button showFriendRequestMenu;
    @FXML
    Button showAllUsersMenu;
    @FXML
    Button showMessagesMenu;
    @FXML
    Button showEventsMenu;
    @FXML
    Button showReportsMenu;
    @FXML
    HBox titleBar;

    Button lastClicked;
    DropShadow shadow = new DropShadow();


    @FXML
    BorderPane view;

    public void init() throws IOException {
        initFriendsStage();
        initUsersStage();
        initRequestsStage();
        initInboxStage();
        initEventStage();
        initReportStage();
        openNotifications=false;

        notificationListView.setItems(notifications);
        notificationListView.setCellFactory(new Callback<ListView<Notification>, ListCell<Notification>>() {
            @Override
            public ListCell<Notification> call(ListView<Notification> param) {
                return new ListCell<Notification>(){
                    @Override
                    protected void updateItem(Notification item, boolean empty){
                        super.updateItem(item,empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Text text = new Text(item.toString());
                            text.setWrappingWidth(180);
                            text.setTextAlignment(TextAlignment.CENTER);
                            setGraphic(text);
                        }
                    }
                };
            }
        });

        showFriendsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setClicked(showFriendsMenu);
                view.setCenter(friendsPane);
            }
        });
        showFriendRequestMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setClicked(showFriendRequestMenu);
                superService.setRequestSeen(loggedUser);
                view.setCenter(requestPane);
            }
        });
        showAllUsersMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setClicked(showAllUsersMenu);
                view.setCenter(usersPane);
            }
        });
        showMessagesMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setClicked(showMessagesMenu);
                superService.setMessageSeen(loggedUser);
                view.setCenter(inboxPane);
            }
        });
        showEventsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setClicked(showEventsMenu);
                view.setCenter(eventPane);
            }
        });
        showReportsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setClicked(showReportsMenu);
                view.setCenter(reportPane);
            }
        });
        mainStage.initStyle(StageStyle.TRANSPARENT);
        shadow.setWidth(0);
        shadow.setHeight(40);
        titleBar.setEffect(shadow);
        superService.addObserver(this);
        refresh();
        //view.setCenter(conversationPane);
    }

    public SuperService getSuperService() {
        return superService;
    }

    public void setSuperService(SuperService superService) {
        this.superService = superService;
    }

    public UserDTO getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(UserDTO loggedUser) {
        this.loggedUser = loggedUser;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Stage getLoginStage() {
        return loginStage;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    private void initFriendsStage() throws IOException {
        friendsStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/FriendsView.fxml"));

        friendsPane = loader.load();
        friendsStage.setScene(new Scene(friendsPane));
        friendsStage.setTitle("Friends");

        friendsController= (FriendsController) loader.getController();
        friendsController.setSuperService(superService);
        friendsController.setUsername(loggedUser.getUsername());
        friendsController.setStage(friendsStage);
        friendsController.initialise();
        friendsController.refresh();

        superService.addObserver(friendsController);
    }

    private void initUsersStage() throws IOException{
        usersStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/UsersView.fxml"));

        usersPane = loader.load();
        usersStage.setScene(new Scene(usersPane));
        usersStage.setTitle("Users");

        usersController= (UsersController) loader.getController();
        usersController.setSuperService(superService);
        usersController.setUsername(loggedUser.getUsername());
        usersController.setStage(usersStage);
        usersController.initialise();
        usersController.refresh();

        superService.addObserver(usersController);
    }

    private void initRequestsStage() throws IOException{
        requestsStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/RequestView.fxml"));

        requestPane = loader.load();
        requestsStage.setScene(new Scene(requestPane));
        requestsStage.setTitle("Users");

        requestController= (RequestController) loader.getController();
        requestController.setSuperService(superService);
        requestController.setUsername(loggedUser.getUsername());
        requestController.setStage(requestsStage);
        requestController.initialise();
        requestController.refresh();

        superService.addObserver(requestController);
    }

    private void initInboxStage() throws IOException {
        inboxStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/InboxView.fxml"));

        inboxPane = loader.load();
        inboxStage.setScene(new Scene(inboxPane));
        inboxStage.setTitle("Messages");

        inboxController = (InboxController) loader.getController();
        inboxController.setSuperService(superService);
        inboxController.setUsername(loggedUser.getUsername());
        inboxController.setInboxStage(inboxStage);
        inboxController.setInboxPane(inboxPane);
        inboxController.setBorderPane(view);
        inboxController.initialise();
        inboxController.refresh();

        superService.addObserver(inboxController);
    }

    private void initEventStage() throws IOException {
        eventStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/EventView.fxml"));

        eventPane = loader.load();
        eventStage.setScene(new Scene(eventPane));
        eventStage.setTitle("Events");

        eventController = (EventController) loader.getController();
        eventController.setSuperService(superService);
        eventController.setUsername(loggedUser.getUsername());
        eventController.setEventPane(eventPane);
        eventController.setBorderPane(view);
        eventController.initialise();
        eventController.refresh();

        superService.addObserver(eventController);
    }

    private void initReportStage() throws IOException {
        reportStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ro/ubb/map/gtsn/guitoysocialnetwork/views/ReportView.fxml"));

        reportPane = loader.load();

        reportController = (ReportController) loader.getController();
        reportController.setSuperService(superService);
        reportController.setUsername(loggedUser.getUsername());
        reportController.initialise();
        reportController.refresh();

        superService.addObserver(reportController);
    }

    public void onExitButtonClick() {
        loginStage.show();
        friendsStage.hide();
        usersStage.hide();
        requestsStage.hide();
        mainStage.hide();
        inboxStage.hide();
        reportStage.hide();
    }

    public void dragged(javafx.scene.input.MouseEvent mouseEvent) {
        mainStage.setX(mouseEvent.getScreenX()-x);
        mainStage.setY(mouseEvent.getScreenY()-y);
    }

    public void pressed(javafx.scene.input.MouseEvent mouseEvent) {
        x = mouseEvent.getSceneX();
        y = mouseEvent.getSceneY();
    }

    public void clickMinimize(MouseEvent mouseEvent) {
        mainStage.setIconified(true);
    }

    public void clickExit(MouseEvent mouseEvent) {
        mainStage.close();
    }

    public void onNotificationButtonClick(){
        if(!openNotifications) {
            notificationListView.setPrefWidth(200);
            notificationListView.setVisible(true);
            openNotifications = true;
        }
        else{
            superService.setEventSeen(loggedUser);
            superService.setRequestSeen(loggedUser);
            superService.setMessageSeen(loggedUser);
            refresh();
            notificationListView.setPrefWidth(0);
            //scClose.play();
            notificationListView.setVisible(false);
            openNotifications = false;
        }
    }

    @Override
    public void update() {
        refresh();
    }

    public void refresh(){
        userPage = superService.getUserPage(loggedUser);
        notifications.setAll(userPage.getNotifications());
        int size = notifications.size();
        if(size==0){
            notificationButton.setText("\uD83D\uDD14");
        }
        else if(size<10){
            notificationButton.setText("\uD83D\uDD14 "+String.valueOf(size));
        }
        else{
            notificationButton.setText("\uD83D\uDD14 9+");
        }
        showFriendsMenu.setText("Friends "+String.valueOf(userPage.getFriendCount()));
        if(userPage.getNewMessageCount()>0) {
            showMessagesMenu.setText("Messages "+String.valueOf(userPage.getNewMessageCount()));
        }
        else {
            showMessagesMenu.setText("Messages");
        }
        if(userPage.getNewRequestCount()>0){
            showFriendRequestMenu.setText("Friend Requests "+ String.valueOf(userPage.getNewRequestCount()));
        }
        else{
            showFriendRequestMenu.setText("Friend Requests");
        }
        loggedAs.setText(userPage.getUser().getFirstname() + " " + userPage.getUser().getLastname());
    }

    private void setClicked(Button button){
        if(lastClicked!=null) {
            //lastClicked.setStyle("-fx-background-color: transparent");
            lastClicked.getStyleClass().clear();
            lastClicked.getStyleClass().add("menuButton");
            lastClicked.setEffect(null);
        }
        lastClicked = button;
        button.getStyleClass().clear();
        button.getStyleClass().add("menuButtonClicked");
        button.setEffect(shadow);
    }

}
