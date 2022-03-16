package ro.ubb.map.gtsn.guitoysocialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.controllers.LogInController;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Friendship;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.*;

import java.io.IOException;
import java.sql.SQLException;

public class ToySocialNetworkApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try{
            String url = "jdbc:postgresql://localhost:5432/ToySocialNetwork";
            String user = "postgres";
            String pass = "postgres";

            UserRepo userRepository = new UserRepo(Constants.PAGE_SIZE, url, user, pass);
            FriendshipRepo friendshipRepository = new FriendshipRepo(Constants.PAGE_SIZE, url, user, pass);
            MessageRepo messageRepo = new MessageRepo(Constants.PAGE_SIZE, url, user, pass);
            RequestRepo requestRepo = new RequestRepo(Constants.PAGE_SIZE, url, user, pass);
            EventRepo eventRepo = new EventRepo(Constants.PAGE_SIZE, url, user, pass);

            Network userNetwork = new Network(userRepository, friendshipRepository);

            UserService userService = new UserService(userRepository, friendshipRepository);
            FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository, requestRepo);
            MessageService messageService = new MessageService(messageRepo);
            EventService eventService = new EventService(eventRepo);

            Validator<Message> messageValidator = new MessageValidator();
            Validator<User> userValidator = new UserValidator();
            Validator<Friendship> friendshipValidator = new FriendshipValidator();
            Validator<Event> eventValidator = new EventValidator();

            SuperService superService = new SuperService(userNetwork, userService, friendshipService, messageService, eventService, userValidator, friendshipValidator, messageValidator, eventValidator);

            Stage login = getLoginStage(superService);
            login.setResizable(true);
            login.show();
        }
        catch (SQLException err){
            err.printStackTrace();
        }
    }

    private Stage getLoginStage(SuperService superService) throws IOException {
        Stage login = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/LogInView.fxml"));

        AnchorPane pane = loader.load();
        login.setScene(new Scene(pane));
        login.setTitle("LogIn");

        LogInController loginController = loader.getController();
        loginController.setSuperService(superService);
        loginController.setLoginStage(login);

        login.initStyle(StageStyle.TRANSPARENT);
        return login;
    }

    public static void main(String[] args) {
        launch();
    }
}
