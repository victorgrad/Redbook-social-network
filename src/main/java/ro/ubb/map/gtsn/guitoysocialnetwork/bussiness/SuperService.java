package ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.EventDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.FriendshipDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.RequestDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators.Validator;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observable;
import ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SuperService implements Observable {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final EventService eventService;
    private final Validator<User> userValidator;
    private final Validator<Friendship> friendshipValidator;
    private final Validator<Message> messageValidator;
    private final Validator<Event> eventValidator;
    private final Network userNetwork;

    private ReportService reportService;

    List<Observer> observerList;

    @Override
    public void addObserver(ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer o) {
        observerList.add(o);
    }

    @Override
    public void removeObserver(ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer o) {
        observerList.remove(o);
    }

    @Override
    public void notifyAllObservers() {
        observerList.forEach(ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer.Observer::update);
    }


    /**
     * Class constructor
     * @param userNetwork, a Network object
     * @param userService, a UserService object
     * @param friendshipService, a FrienshipService object
     * @param messageService, a MessageService object
     * @param eventService, a EventService object
     * @param userValidator, a user validator
     * @param friendshipValidator, a friendship validator
     * @param messageValidator, a message validator
     */
    public SuperService(Network userNetwork,
                        UserService userService, FriendshipService friendshipService, MessageService messageService,
                        EventService eventService,Validator<User> userValidator, Validator<Friendship> friendshipValidator,
                        Validator<Message> messageValidator,Validator<Event> eventValidator){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventService = eventService;
        this.userValidator = userValidator;
        this.friendshipValidator = friendshipValidator;
        this.messageValidator = messageValidator;
        this.eventValidator = eventValidator;
        this.userNetwork = userNetwork;

        this.observerList = new ArrayList<>();

        this.reportService = new ReportService(friendshipService, messageService, userService);
    }

    /**
     * Checks if the username and the password are correct
     * @param userDTO a DTO that contains the username and password hash
     * @return true if the credentials are correct, false otherwise
     */
    public boolean checkPassword(UserDTO userDTO){
        return userService.checkPassword(userDTO);
    }

    /**
     * Creates an iterable of UserDTO objects that contain all the users
     * @return an iterable of UserDTO objects
     */
    public Iterable<UserDTO> getAllUsers(){
        Iterable<User> users = userService.getAllUsers();
        List<UserDTO> userDTOList = new ArrayList<>();
        for(User u: users){
            userDTOList.add(new UserDTO(u));
        }
        return userDTOList;
    }

    /**
     * Returns all events that have not yet passed
     * @return Set<Event>
     */
    public Set<Event> getAllExistingEvents(){
        return eventService.getAllExistingEvents();
    }

    public Set<Event> getExistingEventsPage(int page){
        return eventService.getExistingEventsPage(page);
    }

    /**
     * Returns wether the user is subscribed to an event or not
     * @param username String, the username of the user we are interested in
     * @param eventID Long, the id of the event
     * @return boolean
     */
    public boolean isUserSubscribedTo(String username,Long eventID) {
        return eventService.isUserSubcribedTo(username, eventID);
    }

    /**
     * Creates an iterable of UserDTO objects that contain all the users from the specified page
     * @return an iterable of UserDTO objects
     */
    public Iterable<UserDTO> getUsersPage(int page){
        Iterable<User> users = userService.getUsersPage(page);
        List<UserDTO> userDTOList = new ArrayList<>();
        for(User u: users){
            userDTOList.add(new UserDTO(u));
        }
        return userDTOList;
    }

    /**
     * Returns the users whose username, firstname or lastname starts with the string Name
     * @param name String the filter string
     * @param page int the page number
     * @return list of UserDTOs
     */
    public List<UserDTO> getUserPageByName(String name, int page){
        if(name == null){
            name = "";
        }

        List<UserDTO> userDTOS = new ArrayList<>();
        for(User u: userService.getUserPageByName(name ,page)){
            userDTOS.add(new UserDTO(u));

        }
        return userDTOS;
    }

    public List<UserDTO> getUserByName(String name){
        if(name == null){
            name = "";
        }
        List<UserDTO> userDTOS = new ArrayList<>();

        int page = 0;
        List<User> userList = null;

        do {
            userList = userService.getUserPageByName(name, page);
            page += 1;
            for (User u : userList) {
                userDTOS.add(new UserDTO(u));

            }
        }while (!userList.isEmpty());
        return userDTOS;
    }

    /**
     * Creates a new User object, validates it and adds it in the repository
     * @param userdto UserDTO, a DTO of the user we are adding
     */
    public void addUser(UserDTO userdto) {
        if(userdto.getPasswordHash() == null){
            throw new ValidationException("Password must not be null");
        }
        User user = new User(userdto.getUsername(),userdto.getFirstname(), userdto.getLastname());
        user.setPasswordHash(userdto.getPasswordHash());
        userValidator.validate(user);
        userService.addUser(user);

        notifyAllObservers();
    }

    /**
     * Searches for a specific user
     * @param userDTO a DTO that contains the username of the wanted user
     * @throws NotFoundException if the user is not found
     */
    public UserDTO getUser(UserDTO userDTO) {
        return new UserDTO(userService.getUser(userDTO.getUsername()));
    }

    /**
     * Creates an iterable of FriendshipDTO objects that contain all the friendships
     * @return an iterable of FriendshipDTO objects
     */
    public Iterable<FriendshipDTO> getAllFriendships(){
        Iterable<Friendship> friendships = friendshipService.getAllFriendships();
        List<FriendshipDTO> friendshipDTOList = new ArrayList<>();
        for(Friendship f: friendships){
            UserDTO user1 = new UserDTO(userService.getUser(f.getId().getLeft()));
            UserDTO user2 = new UserDTO(userService.getUser(f.getId().getRight()));
            friendshipDTOList.add(new FriendshipDTO(user1, user2, f.getFriendshipDate()));
        }
        return friendshipDTOList;
    }

    /**
     * Creates an iterable of FriendshipDTO objects that contain all the friendships from the specified page
     * @return an iterable of FriendshipDTO objects
     */
    public Iterable<FriendshipDTO> getFriendshipsPage(int page){
        Iterable<Friendship> friendships = friendshipService.getFriendshipPage(page);
        List<FriendshipDTO> friendshipDTOList = new ArrayList<>();
        for(Friendship f: friendships){
            UserDTO user1 = new UserDTO(userService.getUser(f.getId().getLeft()));
            UserDTO user2 = new UserDTO(userService.getUser(f.getId().getRight()));
            friendshipDTOList.add(new FriendshipDTO(user1, user2, f.getFriendshipDate()));
        }
        return friendshipDTOList;
    }

    /**
     * Searches the friendship between two users
     * @param friendshipDTO DTO that contains the UserDTOs of the users
     * @return a FriendshipDTO object that contains information about the users and their friendship
     * @throws NotFoundException if the users don't exist or aren't friends
     */
    public FriendshipDTO getFriendship(FriendshipDTO friendshipDTO){
        Pair<String> friendshipID = new Pair<>(friendshipDTO.getUser1().getUsername(), friendshipDTO.getUser2().getUsername());
        Friendship friendship = friendshipService.getFriendship(friendshipID);
        UserDTO user1 = new UserDTO(userService.getUser(friendship.getId().getLeft()));
        UserDTO user2 = new UserDTO(userService.getUser(friendship.getId().getRight()));

        return new FriendshipDTO(user1, user2, friendship.getFriendshipDate());
    }

    /**
     * Creates an iterable of FriendshipsDTOs that contain on user2 parameter the friend of the requested user
     * @param userDTO a DTO that contains the username of the user
     * @return an iterable af FriendshipDTO objects
     * @throws NotFoundException if the user with the id userID is not found
     */
    public Iterable<FriendshipDTO> getUserFriends(UserDTO userDTO) {
        String userID = userDTO.getUsername();
        List<Friendship> friendships=new ArrayList<Friendship>(friendshipService.getAllFriendships());

        List<FriendshipDTO> friendsWithDates = new ArrayList<>();
        Predicate<Friendship> pFriend = x -> (x.getId().getLeft().equals(userID) || x.getId().getRight().equals(userID));

        friendships.stream()
                .filter(pFriend)
                .forEach(x ->{
                    FriendshipDTO aux = new FriendshipDTO();
                    if(x.getId().getLeft().equals(userID)){
                        aux.setUser2(new UserDTO(userService.getUser(x.getId().getRight())));
                    }
                    else{
                        aux.setUser2(new UserDTO(userService.getUser(x.getId().getLeft())));
                    }
                    aux.setDate(x.getFriendshipDate());
                    friendsWithDates.add(aux);
                });
        return friendsWithDates;
    }

    /**
     * Creates an iterable of FriendshipsDTOs that contain on user2 parameter the friend of the requested user from the specified page
     * @param userDTO a DTO that contains the username of the user
     * @return an iterable af FriendshipDTO objects
     * @throws NotFoundException if the user with the id userID is not found
     */
    public Iterable<FriendshipDTO> getUserFriendsPage(UserDTO userDTO, int page){
        String userId = userDTO.getUsername();
        Iterable<User> friendsPage = friendshipService.getUserFriendsPage(userId, page);
        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();

        for(User user: friendsPage){
            FriendshipDTO friendshipDTO = new FriendshipDTO();
            friendshipDTO.setUser2(new UserDTO(user));
            friendshipDTOS.add(friendshipDTO);
        }

        return friendshipDTOS;
    }

    /**
     * Returns the friendships of a user, created in the specified month
     * @param userDTO UserDTO that contains the username of the user
     * @param month integer the number of the month
     * @return iterable that contains friends of the specified user
     */
    public Iterable<FriendshipDTO> getUserFriendshipsFromMonth(UserDTO userDTO, int month){
        String userID = userDTO.getUsername();

        if(month < 1 || month > 12){
            throw new ValidationException("Invalid month");
        }
        List<FriendshipDTO> rez = new ArrayList<>();

        Set<Friendship> friendships = friendshipService.getAllFriendships();
        Predicate<Friendship> userPredicate = x -> (userID.equals(x.getId().getLeft()) || userID.equals(x.getId().getRight()));
        Predicate<Friendship> datePredicate = x -> (x.getFriendshipDate().getMonthValue() == month);
        Predicate<Friendship> friendshipPredicate = userPredicate.and(datePredicate);

        friendships.stream()
                .filter(friendshipPredicate)
                .forEach(x -> {
                    FriendshipDTO friend = new FriendshipDTO();
                    UserDTO user2;
                    if(x.getId().getLeft().equals(userID)){
                        user2 = new UserDTO(userService.getUser(x.getId().getRight()));
                    }
                    else{
                        user2 = new UserDTO(userService.getUser(x.getId().getLeft()));
                    }
                    friend.setUser2(user2);
                    friend.setDate(x.getFriendshipDate());
                    rez.add(friend);
                });

        return rez;
    }

    /**
     * Adds a friendship between the users with the ids userID1 and userID2 provided in the DTO
     * @param friendshipdto FriendshipDTO, DTO of the friendship
     * @throws NotFoundException if the users are not found
     * @throws ValidationException if the friendship is not valid
     */
    public void addFriendship(FriendshipDTO friendshipdto) {
        Friendship friendship = new Friendship(friendshipdto.getUser1().getUsername(), friendshipdto.getUser2().getUsername(), LocalDate.now());
        friendshipValidator.validate(friendship);
        friendshipService.addFriendship(friendship);

        notifyAllObservers();
    }

    /**
     * Removes the user with the id userID provided in the DTO
     * @param userdto UserDTO, DTO of the user
     * @throws NotFoundException if the user does not exist
     */
    public void removeUser(UserDTO userdto) {
        userService.removeUser(userdto.getUsername());

        notifyAllObservers();
    }

    /**
     * Removes the friendship provided with the DTO
     * @param friendshipdto FriendshipDTO, DTO of the Friendship
     * @throws NotFoundException if the users do not exist or are not friends
     */
    public void removeFriendship(FriendshipDTO friendshipdto) {
        friendshipService.removeFriendship(new Pair<String>(friendshipdto.getUser1().getUsername(), friendshipdto.getUser2().getUsername()));
        notifyAllObservers();
    }

    /**
     * Updates the data of the user with the data from userdto
     * @param userdto the dto that contains the new data
     */
    public void updateUser(UserDTO userdto){
        User user = new User(userdto.getUsername(), userdto.getFirstname(), userdto.getLastname());
        userValidator.validate(user);
        userService.updateUser(user);

        notifyAllObservers();
    }

    /**
     * Computes the number of communities between users
     * @return the number of communities
     */
    public int numberOfCommunities(){
        return userNetwork.getConnexComponents().size();
    }

    /**
     * Finds the most sociable community: the community that contains the longest elementary path between its users
     * @return a tuple with the length of the path and the users in the community
     */
    public Tuple<Integer, List<UserDTO>> getMostSociableCommunity(){
        Tuple<Integer, List<String>> mostSociable = userNetwork.getMostSociableComponent();
        List<UserDTO> userDTOList = new ArrayList<>();

        for(String username: mostSociable.getSecond()){
            userDTOList.add(new UserDTO(userService.getUser(username)));
        }
        return new Tuple<>(mostSociable.getFirst(), userDTOList);
    }

    /**
     * Sends the message
     * @param from String - id of the user that sends the message
     * @param text String - the main body of the message
     * @param to List of strings - a list of recipients
     * @return Message the newly generated message
     */
    public Message sendMessage(String from, String text, List<String> to){
        LocalDateTime localDateTime = LocalDateTime.now();
        Set<String> toSet = new HashSet<>(to);
        Message message = new Message(0L, from, text, localDateTime);
        message.setTo(toSet);
        messageValidator.validate(message);
        message.setId(messageService.sendMessage(message));

        notifyAllObservers();

        return message;
    }

    /**
     * Replies to a message by sending a message to the person that wrote the initial one
     * @param from String - id of the user that sends the message
     * @param text String - the main body of the message
     * @param originalMessageId Long - id of the message which we are replying to
     * @return Message - the generated message
     */
    public Message replyMessage(String from, String text, Long originalMessageId){
        LocalDateTime localDateTime = LocalDateTime.now();
        Message originalMessage = messageService.getMessage(originalMessageId);

        if(!(originalMessage.getTo().contains(from) || originalMessage.getFrom().equals(from))){
            throw new ValidationException("User can't reply a message didn't get");
        }

        Set<String> toSet = new HashSet<>();
        toSet.add(originalMessage.getFrom());
        Message message = new Message(0L,from,text,localDateTime, originalMessageId);
        message.setTo(toSet);
        messageValidator.validate(message);
        message.setId(messageService.sendMessage(message));

        notifyAllObservers();

        return message;
    }

    /**
     * Replies to a message by sending a message to all the users that received the initial one
     * @param from String - id of the user that sends the message
     * @param text String - the main body of the message
     * @param originalMessageId Long - id of the message which we are replying to
     * @return Message - the generated message
     */
    public Message replyAllMessage(String from, String text, Long originalMessageId){
        LocalDateTime localDateTime = LocalDateTime.now();
        Message originalMessage = messageService.getMessage(originalMessageId);

        if(!(originalMessage.getTo().contains(from) || originalMessage.getFrom().equals(from))){
            throw new ValidationException("User can't reply a message didn't get");
        }

        Set<String> toSet = new HashSet<>(originalMessage.getTo());
        toSet.add(originalMessage.getFrom());
        toSet.remove(from);
        Message message = new Message(0L,from,text,localDateTime, originalMessageId);
        message.setTo(toSet);
        messageValidator.validate(message);
        message.setId(messageService.sendMessage(message));

        notifyAllObservers();

        return message;
    }

    /**
     * Returns the set of messages that make up the conversation
     * @param userId1 String, the id of the first user
     * @param userId2 String,  the id of the second user
     * @return Set<Message>
     * @throws NotFoundException - if there are no messages between the 2 users or the users don't exist
     */
    public Set<Message> getConversation(String userId1, String userId2){
        return messageService.getConversation(userId1,userId2);
    }

    /**
     * Returns the set of messages that make up the conversation from the specified page
     * @param userId1 String, the id of the first user
     * @param userId2 String,  the id of the second user
     * @return Set<Message>
     * @throws NotFoundException - if there are no messages between the 2 users or the users don't exist
     */
    public Set<Message> getConversationPage(String userId1, String userId2, int page){
        return messageService.getConversationPage(userId1, userId2, page);
    }

    /**
     * Returns the set of messages that were sent or received by a user
     * @param userDTO, a DTO that contains the username of the user
     * @return Set<Message>
     * @throws NotFoundException - if there are no messages sent or received by the user or the user doesn't exist
     */
    public Set<Message> getUserMessages(UserDTO userDTO) {
        return messageService.getUserMessages(userDTO.getUsername());
    }

    public Set<Message> getLastUserMessagesFromChainPage(UserDTO userDTO, int page){
        return messageService.getLastUserMessagesFromChainPage(userDTO.getUsername(), page);
    }

    /**
     * Returns all the messages above the requested one in a reply chain
     * @param messageID the last massage in the reply chain
     * @return list of messages
     */
    public List<Message> getMessageReplyThread(Long messageID){
        LinkedList<Message> thread = new LinkedList<>();

        Long replying = messageID;
        Message message;

        while (replying != 0){
            message = messageService.getMessage(replying);
            replying = message.getReplyToMessageId();
            thread.addFirst(message);
        }

        return thread;
    }

    /**
     * Returns the list of events a user is subcribed to
     * @param userDTO, a DTO that contains the username of the user
     * @return Set<Message>
     */
    public Set<Event> getUserEvents(UserDTO userDTO) {
        return eventService.getUserEvents(userDTO.getUsername());
    }

    /**
     * Returns the set of messages that were sent or received by a user from the specified page
     * @param userDTO, a DTO that contains the username of the user
     * @return Set<Message>
     * @throws NotFoundException - if there are no messages sent or received by the user or the user doesn't exist
     */
    public Set<Message> getUserMessagesPage(UserDTO userDTO, int page){
        return messageService.getUserMessagesPage(userDTO.getUsername(), page);
    }

    /**
     * Returns the Message object for the Message id provided
     * @param messageID - a Long
     * @return an instance of Message
     * @throws NotFoundException - if the message does not exist
     */
    public Message getMessage(Long messageID) {
        return messageService.getMessage(messageID);
    }

    /**
     * Saves a new friendship request from the first user
     * @param requestDTO, RequestDTO of the request to be added
     * @throws NotFoundException if one of the users does not exist
     * @throws DuplicateException if the users are already friends or a request that is not rejected already exists
     */
    public void addFriendRequest(RequestDTO requestDTO){
        friendshipService.addFriendshipRequest(requestDTO.getFrom().getUsername(),requestDTO.getTo().getUsername());

        notifyAllObservers();
    }

    /**
     * Removes an existing friendship request from the first user
     * @param requestDTO, RequestDTO of the request to be removed
     * @throws NotFoundException if one of the users does not exist
     * @throws DuplicateException if the users are already friends or a request that is not rejected already exists
     */
    public void removeFriendRequest(RequestDTO requestDTO){
        friendshipService.removeFriendRequest(requestDTO.getFrom().getUsername(),requestDTO.getTo().getUsername());

        notifyAllObservers();
    }

    /**
     * Accepts the request sent by the first user to the second user
     * @param requestDTO RequestDTO, accepts the request corresponding to the DTO
     * @throws NotFoundException if the request is not found
     */
    public void acceptFriendRequest(RequestDTO requestDTO){
        friendshipService.acceptFriendshipRequest(requestDTO.getFrom().getUsername(),requestDTO.getTo().getUsername());

        notifyAllObservers();
    }

    /**
     * Rejects the request sent by the first user to the second user
     * @param requestDTO RequestDTO, rejects the request corresponding to the DTO
     * @throws NotFoundException if the request is not found
     */
    public void rejectFriendRequest(RequestDTO requestDTO){
        friendshipService.rejectFriendRequest(requestDTO.getFrom().getUsername(),requestDTO.getTo().getUsername());

        notifyAllObservers();
    }

    /**
     * Returns a list with all the requests that can be accepted by the user
     * @param userDTO a DTO that contains the username of the user
     * @return List of RequestDTO
     * @throws NotFoundException if the user does not exist
     */
    public List<RequestDTO> getUserFriendRequests(UserDTO userDTO){
        String userID = userDTO.getUsername();
        User to = userService.getUser(userID);
        List<RequestDTO> rez = new ArrayList<>();
        friendshipService.getUserFriendRequests(userID).forEach(request -> {
            User from = userService.getUser(request.getFrom());

            rez.add(new RequestDTO(new UserDTO(from), new UserDTO(to), request.getStatus(), request.getLocalDateTime()));
        });

        return rez;
    }

    public void addEvent(EventDTO eventDTO){
        Event event = new Event(eventDTO.getName(),eventDTO.getDateTime(),eventDTO.getDescription(),eventDTO.getLocation());
        eventValidator.validate(event);
        eventService.addEvent(event);

        notifyAllObservers();
    }

    public EventDTO getEvent(Long eventId){
        Event event = eventService.getEvent(eventId);
        return new EventDTO(event);
    }


    public void addEventParticipant(Long eventId,String username,Boolean toNotify){
        if(userService.userExists(username) && eventService.eventExists(eventId)){
            eventService.addEventParticipant(eventId,username,toNotify);

            notifyAllObservers();
        } else
            throw new NotFoundException("Event or Username not found");
    }


    /**
     * Returns a list with all the requests that can be accepted by the user from the specified page
     * @param userDTO a DTO that contains the username of the user
     * @return List of RequestDTO
     * @throws NotFoundException if the user does not exist
     */
    public List<RequestDTO> getUserFriendRequestsPage(UserDTO userDTO, int page){
        String userID = userDTO.getUsername();
        User to = userService.getUser(userID);
        List<RequestDTO> rez = new ArrayList<>();
        friendshipService.getUserFriendRequestsPage(userID, page).forEach(request -> {
            User from = userService.getUser(request.getFrom());

            rez.add(new RequestDTO(new UserDTO(from), new UserDTO(to), request.getStatus(), request.getLocalDateTime()));
        });

        return rez;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public Page getUserPage(UserDTO userDTO){
        Set<Notification> notifications = new HashSet<>();
        User userAux = userService.getUser(userDTO.getUsername());
        UserDTO user = new UserDTO(userAux);
        Set<Notification> eventNotifications = eventService.getUserNotifications(user.getUsername());
        Set<Notification> requestNotifications = friendshipService.getUserNotifications(user.getUsername());
        Set<Notification> messageNotifications = messageService.getUserNotifications(user.getUsername());
        notifications.addAll(eventNotifications);
        notifications.addAll(requestNotifications);
        notifications.addAll(messageNotifications);
        int requestCount = requestNotifications.size();
        int messageCount = messageNotifications.size();
        int friendCount = friendshipService.getUserFriendCount(user.getUsername());
        return new Page(user,notifications.stream().sorted((n1,n2)-> n1.getLocalDateTime().compareTo(n2.getLocalDateTime())
        ).collect(Collectors.toList()), friendCount,messageCount,requestCount);
    }

    public void setEventSeen(UserDTO user){
        eventService.setSeen(user.getUsername());
    }

    public long getUserPageNumber(){
        return userService.getSize() / Constants.PAGE_SIZE;
    }

    public long getMessagePageNumber(String username){
        userService.getUser(username);
        return messageService.getUserMessageNumber(username) / Constants.PAGE_SIZE;
    }

    public long getFriendshipPageNumber(String username){
        userService.getUser(username);
        return friendshipService.getUserFriendshipNumber(username) / Constants.PAGE_SIZE;
    }

    public long getRequestPageNumber(String username){
        userService.getUser(username);
        return friendshipService.getUserRequestNumber(username) / Constants.PAGE_SIZE;
    }

    public void setRequestSeen(UserDTO user){
        friendshipService.setSeen(user.getUsername());
        notifyAllObservers();
    }

    public void setMessageSeen(UserDTO user){
        messageService.setSeen(user.getUsername());
        notifyAllObservers();
    }

    public long getExistingEventsPageNumber(){
        return eventService.getExistingNumber() / Constants.PAGE_SIZE;
    }
}
