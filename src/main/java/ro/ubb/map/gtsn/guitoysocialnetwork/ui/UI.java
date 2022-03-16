package ro.ubb.map.gtsn.guitoysocialnetwork.ui;

import ro.ubb.map.gtsn.guitoysocialnetwork.bussiness.SuperService;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.FriendshipDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.RequestDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.Encrypt;

import java.util.*;

public class UI {
    private final SuperService superService;
    Scanner console;

    public UI(SuperService superService){
        this.superService = superService;
        console = new Scanner(System.in);
    }

    private void printUser(UserDTO userDTO){
        System.out.print("'" + userDTO.getUsername() + "': " + userDTO.getFirstname() + " " + userDTO.getLastname());
    }

    private void printFriendship(FriendshipDTO friendshipDTO){
        printUser(friendshipDTO.getUser1());
        System.out.print(" | ");
        printUser(friendshipDTO.getUser2());
        System.out.print(" | ");
        if(friendshipDTO.getDate() != null){
            System.out.print(friendshipDTO.getDate().format(Constants.DATE_TIME_FORMATTER));
        }
    }

    private void showUsers(){
        System.out.println("Users:");

        superService.getAllUsers().forEach(user -> {
            printUser(user);
            System.out.println();
        });
    }

    private void showFriends(){
        System.out.print("Username: ");
        String userID = console.nextLine();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userID);
        UserDTO user = superService.getUser(userDTO);

        if(user == null){
            System.out.println("User not found");
            return;
        }

        printUser(user);
        System.out.println();
        System.out.print("Friends: \n");
        superService.getUserFriends(userDTO).forEach(friendshipDTO -> {
            printUser(friendshipDTO.getUser2());
            System.out.println(" | " + friendshipDTO.getDate().format(Constants.DATE_TIME_FORMATTER));

        });
        System.out.println();
    }

    private void showFriendsByMonth(){
        System.out.print("Username: ");
        String userID = console.nextLine();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userID);

        System.out.print("Month: ");
        String month = console.nextLine();

        int m = 0;

        try {
            m = Integer.parseInt(month);
        } catch (NumberFormatException err){
            throw new ValidationException("Invalid month");
        }

        UserDTO user = superService.getUser(userDTO);

        if(user == null){
            System.out.println("User not found");
            return;
        }

        printUser(user);
        System.out.println();
        System.out.print("Friends: \n");

        superService.getUserFriendshipsFromMonth(userDTO, m)
                .forEach(friendshipDTO -> {
                    printUser(friendshipDTO.getUser2());
                    System.out.println(" " + friendshipDTO.getDate().format(Constants.DATE_TIME_FORMATTER));
                });
        System.out.println();
    }

    private UserDTO readUserData(){
        UserDTO userDTO = new UserDTO();
        System.out.print("Username: ");
        userDTO.setUsername(console.nextLine());
        System.out.print("Firstname: ");
        userDTO.setFirstname(console.nextLine());
        System.out.print("Lastname: ");
        userDTO.setLastname(console.nextLine());

        return userDTO;
    }

    private void checkUserPassword(){
        System.out.print("Username: ");
        String username = console.nextLine();

        System.out.print("Password: ");
        String pass = console.nextLine();

        String passHash = Encrypt.getSha(pass);
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setPasswordHash(passHash);

        if(superService.checkPassword(userDTO)){
            System.out.println("Correct credentials");
        }
        else{
            System.out.println("Incorrect credentials");
        }
    }

    private void addUser(){
        UserDTO userDTO = readUserData();

        System.out.print("Password: ");
        String password = console.nextLine();
        String passHash = Encrypt.getSha(password);

        userDTO.setPasswordHash(passHash);

        superService.addUser(userDTO);
        System.out.println("User added successfully");
    }


    private void updateUser(){
        UserDTO userDTO = readUserData();
        superService.updateUser(userDTO);
        System.out.println("User updated successfully");
    }

    private void removeUser(){
        UserDTO user = new UserDTO();
        System.out.print("Username: ");
        user.setUsername(console.nextLine());

        superService.removeUser(user);
        System.out.println("User removed successfully");
    }

    private void addFriendship(){
        superService.addFriendship(readFriendship());
        System.out.println("Befriended users successfully");
    }

    private void removeFriendship(){
        superService.removeFriendship(readFriendship());
        System.out.println("Removed friendship between users successfully");
    }

    private FriendshipDTO readFriendship() {
        FriendshipDTO friendshipDTO = new FriendshipDTO();
        UserDTO user1 = new UserDTO();
        UserDTO user2 = new UserDTO();

        System.out.print("First username: ");
        user1.setUsername(console.nextLine());
        System.out.print("Second username: ");
        user2.setUsername(console.nextLine());

        friendshipDTO.setUser1(user1);
        friendshipDTO.setUser2(user2);

        return  friendshipDTO;
    }

    private void numberOfCommunities(){
        System.out.println("There are " + superService.numberOfCommunities() + " communities");
    }

    private void mostSociableCommunity(){
        Tuple<Integer, List<UserDTO>> community = superService.getMostSociableCommunity();
        System.out.println("The most sociable community contains a path of " + community.getFirst() + " users");
        System.out.println("The users in this community are: ");
        community.getSecond().forEach(u -> System.out.print(u + "; "));
        System.out.println();
    }

    private void showAllFriendships(){
        System.out.println("Friendships:");
        superService.getAllFriendships().forEach(x -> {printFriendship(x);
            System.out.println();
        });
    }

    private void showFriendship(){
        String userID1, userID2;
        System.out.print("First username: ");
        userID1 = console.nextLine();
        System.out.print("Second username: ");
        userID2 = console.nextLine();

        UserDTO user1 = new UserDTO();
        UserDTO user2 = new UserDTO();

        user1.setUsername(userID1);
        user2.setUsername(userID2);

        FriendshipDTO friendshipDTO = new FriendshipDTO();
        friendshipDTO.setUser1(user1);
        friendshipDTO.setUser2(user2);

        printFriendship(superService.getFriendship(friendshipDTO));
        System.out.println();
    }

    private void sendMessage(){
        String from, to, text;
        System.out.print("From: ");
        from = console.nextLine();
        System.out.println("To (separated by spaces, commas or semicolons): ");
        to = console.nextLine();
        System.out.println("Message: ");
        text = console.nextLine();

        superService.sendMessage(from, text, Arrays.asList(to.split("[,; ]+")));
    }

    private void printMessage(Message message){
        System.out.println("From: " + message.getFrom());
        System.out.print("To: ");
        message.getTo().forEach(username -> System.out.print(username + "; "));
        System.out.println();

        System.out.print(message.getMessageDateTime().format(Constants.DATE_TIME_FORMATTER2));
        System.out.println(" \"" + message.getMessage() + "\"");
    }

    private void printReplyMessage(Message message, Message replying){
        System.out.println("From: " + message.getFrom());
        System.out.print("To: ");
        message.getTo().forEach(username -> System.out.print(username + "; "));
        System.out.println();

        System.out.println("Replying to message:");
        System.out.println("--------------------------------");
        printMessage(replying);
        System.out.println("--------------------------------");

        System.out.print(message.getMessageDateTime().format(Constants.DATE_TIME_FORMATTER2));
        System.out.println(" \"" + message.getMessage() + "\"");
    }

    private void printMessages(Set<Message> messages) {
        List<Message> toShow = new ArrayList<>(messages);
        int start = toShow.size() - Constants.SHOWING;
        int stop = toShow.size();
        boolean quit = false;

        if(start < 0){
            start = 0;
        }

        while (!quit) {
            printMessageRange(toShow, start, stop);
            boolean ok = false;

            while(!ok) {
                System.out.println();
                System.out.println("p - to see previous messages\nn - to see next messages\nq - to exit this menu");
                System.out.print(">>");
                String response = console.nextLine();
                switch (response) {
                    case "q" -> {
                        quit = true;
                        ok = true;
                    }
                    case "p" -> {
                        if (start == 0) {
                            System.out.println("You have reached the end");
                            break;
                        }
                        stop = start;
                        start = start - Constants.SHOWING;
                        if (start < 0) {
                            start = 0;
                        }
                        ok = true;
                    }
                    case "n" -> {
                        if (stop == toShow.size()) {
                            System.out.println("You have reached the end");
                            break;
                        }
                        start = stop;
                        stop = stop + Constants.SHOWING;
                        if (stop > toShow.size()) {
                            stop = toShow.size();
                        }
                        ok = true;
                    }
                    default -> System.out.println("Invalid command");
                }
            }
        }
    }

    private void showUserMessages(){
        String username;
        System.out.print("Username: ");
        username = console.nextLine();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);

        Set<Message> messages = superService.getUserMessages(userDTO);
        printMessages(messages);
    }

    private void showConversation(){
        String user1, user2;
        System.out.print("First user: ");
        user1 = console.nextLine();
        System.out.print("Second user: ");
        user2 = console.nextLine();

        Set<Message> messages = superService.getConversation(user1, user2);
        printMessages(messages);
    }

    private void printMessageRange(List<Message> messages, int start, int stop){
        System.out.println("Showing messages from " + (start + 1) + " to " + (stop) + ":");
        for (int i = start; i < stop; i++) {
            Long replyTo = messages.get(i).getReplyToMessageId();

            System.out.println("===================================================================");
            System.out.println((i + 1) + ".");
            if (replyTo.equals(0L)) {
                printMessage(messages.get(i));
            } else {
                boolean ok = false;
                for (Message msg : messages) {
                    if (msg.getId().equals(replyTo)) {
                        printReplyMessage(messages.get(i), msg);
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    printMessage(messages.get(i));
                }
            }
            System.out.println("===================================================================");
        }
    }

    private int getMessageIndex(List<Message> messages){
        int start = messages.size() - Constants.SHOWING;
        int stop = messages.size();
        boolean quit = false;

        if(start < 0){
            start = 0;
        }

        while (!quit) {
            printMessageRange(messages, start, stop);

            boolean ok = false;

            while(!ok) {
                System.out.println();
                System.out.println("p - to see previous messages\nn - to see next messages\nq - to abort the operation\ns - to select a message");
                System.out.print(">>");
                String response = console.nextLine();
                switch (response) {
                    case "q" -> {
                        quit = true;
                        ok = true;
                    }
                    case "p" -> {
                        if (start == 0) {
                            System.out.println("You have reached the end");
                            break;
                        }
                        stop = start;
                        start = start - Constants.SHOWING;
                        if (start < 0) {
                            start = 0;
                        }
                        ok = true;
                    }
                    case "n" -> {
                        if (stop == messages.size()) {
                            System.out.println("You have reached the end");
                            break;
                        }
                        start = stop;
                        stop = stop + Constants.SHOWING;
                        if (stop > messages.size()) {
                            stop = messages.size();
                        }
                        ok = true;
                    }
                    case "s" -> {
                        System.out.print(">>");
                        String number = console.nextLine();
                        int value = -1;
                        try {
                            value = Integer.parseInt(number);
                            if (value > 0 && value <= messages.size()) {
                                return value - 1;
                            } else {
                                System.out.println("Invalid value");
                            }
                        } catch (NumberFormatException err){
                            System.out.println("invalid value");
                        }
                    }
                    default -> System.out.println("Invalid command");
                }
            }
        }
        return -1;
    }

    private void sendReply(){
        String user1, user2, text;
        System.out.print("From: ");
        user1 = console.nextLine();
        System.out.print("Conversation with: ");
        user2 = console.nextLine();

        List<Message> messages = new ArrayList<>(superService.getConversation(user1, user2));
        int index = getMessageIndex(messages);

        if(index >= 0) {
            System.out.println("Message: ");
            text = console.nextLine();
            superService.replyMessage(user1, text, messages.get(index).getId());
        }
    }

    private void sendReplyAll() {
        String user1, user2, text;
        System.out.print("From: ");
        user1 = console.nextLine();
        System.out.print("Conversation with: ");
        user2 = console.nextLine();

        List<Message> messages = new ArrayList<>(superService.getConversation(user1, user2));
        int index = getMessageIndex(messages);

        if (index >= 0) {
            System.out.println("Message: ");
            text = console.nextLine();
            superService.replyAllMessage(user1, text, messages.get(index).getId());
        }
    }

    private void addFriendRequest(){
        superService.addFriendRequest(readFriendshipRequest());
        System.out.println("Friend request sent");
    }

    private void acceptFriendRequest(){
        superService.acceptFriendRequest(readFriendshipRequest());
        System.out.println("Request accepted");
    }

    private RequestDTO readFriendshipRequest(){
        UserDTO from = new UserDTO();
        UserDTO to = new UserDTO();

        System.out.print("From: ");
        from.setUsername(console.nextLine());
        System.out.print("To: ");
        to.setUsername(console.nextLine());

        return new RequestDTO(from, to);
    }

    private void rejectFriendRequest(){
        superService.rejectFriendRequest(readFriendshipRequest());
        System.out.println("Request rejected");
    }

    private void showUserFriendRequests(){
        String userId;
        System.out.print("Username: ");
        userId = console.nextLine();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userId);

        List<RequestDTO> requests = superService.getUserFriendRequests(userDTO);

        System.out.println("Requests: ");
        requests.forEach(x -> System.out.println(x.getTo().getUsername() + " status: " + x.getStatus() + " date: " + x.getLocalDateTime().format(Constants.DATE_TIME_FORMATTER)));
    }

    private void help(){
        System.out.println("Supported commands:");
        System.out.println("    help                        - show this menu;");
        System.out.println("    showusers / su              - show all registered users;");
        System.out.println("    showuserfriends / suf       - show all friends of an user;");
        System.out.println("    showallfriendships / saf    - show all the friendships;");
        System.out.println("    showfriendship / sf         - show the friendship between two users;");
        System.out.println("    adduser / au                - register a new user;");
        System.out.println("    updateuser / uu             - update an existing user;");
        System.out.println("    removeuser / ru             - remove an user;");
        System.out.println("    addfriendship / af          - make two users friends;");
        System.out.println("    removefriendship /rf        - remove the friendship between two users;");
        System.out.println("    numberofcommunities / nc    - shows the number of communities");
        System.out.println("    mostsociablecommunity / msc - shows the most sociable communities");
        System.out.println("    sendmessage / sm            - send a message to one or more users");
        System.out.println("    showusermessages / sum      - show all messages of a user");
        System.out.println("    showconversation / sc       - show the conversation between two users");
        System.out.println("    sendreply / sr              - send a reply to the sender of the message");
        System.out.println("    sendreplyall / sra          - send a reply to the sender and all who got the message");
        System.out.println("    addFriendRequest / afr       - send a friend request from a user to another");
        System.out.println("    acceptFriendRequest / acfr   - accept a friend request");
        System.out.println("    rejectFriendRequest / rfr    - reject a friend request");
        System.out.println("    showUserFriendRequests / sfr - shows all the friend requests of a user");
        System.out.println("    showUserFriendRequests / sfr - shows all the friend requests of a user");
        System.out.println("    checkUserPassword / cup      - simulate login");
        System.out.println("    quit / close                 - stop the program;");
        System.out.println();
    }

    public void run(){
        boolean run = true;
        String cmd;
        while (run){
            System.out.print(">>>");
            cmd = console.nextLine();
            try {
                switch (cmd) {
                    case "help"                          -> help();
                    case "quit", "close"                 -> run = false;
                    case "showusers",               "su" -> showUsers();
                    case "showuserfriends",        "suf" -> showFriends();
                    case "showuserfriendsbymonth", "sufm"-> showFriendsByMonth();
                    case "showallfriendships",     "saf" -> showAllFriendships();
                    case "showfriendship",          "sf" -> showFriendship();
                    case "adduser",                 "au" -> addUser();
                    case "updateuser",              "uu" -> updateUser();
                    case "removeuser",              "ru" -> removeUser();
                    case "addfriendship",           "af" -> addFriendship();
                    case "removefriendship",        "rf" -> removeFriendship();
                    case "numberofcommunities",     "nc" -> numberOfCommunities();
                    case "mostsociablecommunity",  "msc" -> mostSociableCommunity();
                    case "sendmessage",             "sm" -> sendMessage();
                    case "showusermessages",       "sum" -> showUserMessages();
                    case "showconversation",        "sc" -> showConversation();
                    case "sendreply",               "sr" -> sendReply();
                    case "sendreplyall",           "sra" -> sendReplyAll();
                    case "addFriendRequest",       "afr" -> addFriendRequest();
                    case "acceptFriendRequest",    "acfr"-> acceptFriendRequest();
                    case "rejectFriendRequest",    "rfr" -> rejectFriendRequest();
                    case "showUserFriendRequest",  "sfr" -> showUserFriendRequests();
                    case "checkUserPassword",      "cup" -> checkUserPassword();
                    default -> System.out.println("Invalid command");
                }
            }catch (ValidationException | NotFoundException | IllegalArgumentException | DuplicateException | RepoException exception){
                System.out.println(exception.getMessage());
            }
        }
        System.out.println("Closed");
        console.close();
    }
}
