package ro.ubb.map.gtsn.guitoysocialnetwork;

import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Friendship;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.ui.UI;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try{
            String url = "jdbc:postgresql://localhost:5432/ToySocialNetwork";
            String user = "postgres";
            String pass = "postgres";

            UserRepo userRepository = new UserRepo(Constants.PAGE_SIZE,url, user, pass);
            FriendshipRepo friendshipRepository = new FriendshipRepo(Constants.PAGE_SIZE,url, user, pass);
            MessageRepo messageRepo = new MessageRepo(Constants.PAGE_SIZE,url, user, pass);
            RequestRepo requestRepo = new RequestRepo(Constants.PAGE_SIZE,url, user, pass);
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

            SuperService superService = new SuperService(userNetwork, userService, friendshipService, messageService,eventService, userValidator, friendshipValidator, messageValidator,eventValidator);
            UI ui = new UI(superService);
            ui.run();
        }
        catch (SQLException err){
            err.printStackTrace();
        }
    }
}
