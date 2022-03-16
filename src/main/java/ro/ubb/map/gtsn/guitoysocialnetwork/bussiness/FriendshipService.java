package ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.*;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.FriendshipRepo;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.RequestRepo;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.UserRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipService {
    private final FriendshipRepo friendshipRepository;
    private final UserRepo userRepository;
    private final RequestRepo requestRepo;

    /**
     * Class constructor
     * @param friendshipRepository - instance of FriendshipRepository
     * @param requestRepo - instance od RequestRepo
     */
    public FriendshipService(FriendshipRepo friendshipRepository, UserRepo userRepository, RequestRepo requestRepo){
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.requestRepo = requestRepo;
    }

    /**
     * Returns the Friendship object for the users with the id-s contained in the pair
     * @param friendshipID - a pair of 2 user id-s
     * @return an instance of Friendship
     * @throws NotFoundException - if the user aren't friends
     */
    public Friendship getFriendship(Pair<String> friendshipID){
        Friendship found = friendshipRepository.findOne(friendshipID);
        if(found == null){
            throw new NotFoundException("Friendship not found");
        }
        return found;
    }

    /**
     * Returns an iterable container that contains all the friendships
     * @return - an iterable container
     */
    public Set<Friendship> getAllFriendships(){
        return friendshipRepository.findAll();
    }

    /**
     * Returns the requested page
     * @param page int the page number
     * @return set of friendships
     * @throws RepoException if page < 0
     */
    public Set<Friendship> getFriendshipPage(int page) {
        return friendshipRepository.findPage(page);
    }

    /**
     * removes from repository the Friendship object with the id equal to friendshipID
     * @param friendshipID - the id of the friendship to be removed
     * @throws NotFoundException - if the friendship doesn't exist
     */
    public void removeFriendship(Pair<String> friendshipID){
        if(!friendshipRepository.exists(friendshipID)){
            throw new NotFoundException("Friendship not found");
        }
        friendshipRepository.delete(friendshipID);
        if(requestRepo.findOne(new Tuple<>(friendshipID.getLeft(), friendshipID.getRight())) != null)
            requestRepo.delete(new Tuple<>(friendshipID.getLeft(), friendshipID.getRight()));

        if(requestRepo.findOne(new Tuple<>(friendshipID.getRight(), friendshipID.getLeft())) != null)
            requestRepo.delete(new Tuple<>(friendshipID.getRight(), friendshipID.getLeft()));
    }

    /**
     * adds the friendship in the repository
     * @param friendship - a valid friendship object
     * @throws DuplicateException - if the friendship already exists
     */
    public void addFriendship(Friendship friendship){
        if(friendshipRepository.exists(friendship.getId())){
            throw new DuplicateException("Friendship already exists");
        }
        friendshipRepository.save(friendship);
    }

    public Iterable<User> getUserFriends(String username){
        Iterable<String> friends = friendshipRepository.getUserFriends(username);
        Set<User> users = new HashSet<>();

        for(String f: friends){
            users.add(userRepository.findOne(f));
        }

        return users;
    }

    public Iterable<User> getUserFriendsPage(String username, int page){
        Iterable<String> friends = friendshipRepository.getUserFriendsPage(username, page);
        Set<User> users = new HashSet<>();

        for(String f: friends){
            users.add(userRepository.findOne(f));
        }

        return users;
    }

    /**
     * Saves a new friendship request from the first user
     * @param from String the username of the user sending te request
     * @param to String the username of the user receiving the request
     * @throws NotFoundException if one of the users does not exist
     * @throws DuplicateException if the users are already friends or a request that is not rejected already exists
     */
    public void addFriendshipRequest(String from, String to){
        Request request = new Request(from, to, Constants.PENDINGREQUEST, LocalDateTime.now());
        if(!userRepository.exists(request.getFrom())){
            throw new NotFoundException("First user not found");
        }
        if(!userRepository.exists(request.getTo())){
            throw new NotFoundException("Second user not found");
        }
        if(friendshipRepository.exists(new Pair<>(request.getFrom(), request.getTo()))){
            throw new DuplicateException("Already friends");
        }

        Request exists = requestRepo.findOne(new Tuple<>(to, from));
        if(exists != null){
            switch (exists.getStatus()){
                case Constants.PENDINGREQUEST -> throw new DuplicateException("Second user already sent a request");
                case Constants.ACCEPTEDREQUEST -> throw new DuplicateException("Already friends");
            }
        }

        Request status = requestRepo.findOne(new Tuple<>(from, to));
        if(status == null) {
            requestRepo.save(request);
        } else {
            switch (status.getStatus()){
                case Constants.ACCEPTEDREQUEST -> throw new DuplicateException("Request is already accepted");
                case Constants.PENDINGREQUEST -> throw new DuplicateException("Request is already pending");
                default -> requestRepo.update(request);
            }
        }
    }

    /**
     * Accepts the request sent by the first user to the second user
     * @param from String the username of the user sending te request
     * @param to String the username of the user receiving the request
     * @throws NotFoundException if the request is not found
     */
    public void acceptFriendshipRequest(String from, String to){
        Request request = requestRepo.findOne(new Tuple<>(from, to));
        if(request == null) {
            throw new NotFoundException("Request not found");
        } else {
            switch (request.getStatus()){
                case Constants.ACCEPTEDREQUEST -> throw new DuplicateException("Request is already accepted");
                case Constants.REJECTEDREQUEST -> throw new DuplicateException("Request is already rejected");
                default -> {
                    request.setStatus(Constants.ACCEPTEDREQUEST);
                    requestRepo.update(request);
                    friendshipRepository.save(new Friendship(from, to, LocalDate.now()));
                }
            }
        }
    }

    /**
     * Rejects the request sent by the first user to the second user
     * @param from String the username of the user sending te request
     * @param to String the username of the user receiving the request
     * @throws NotFoundException if the request is not found
     */
    public void rejectFriendRequest(String from, String to){
        Request request = requestRepo.findOne(new Tuple<>(from, to));
        if(request == null) {
            throw new NotFoundException("Request not found");
        } else {
            switch (request.getStatus()){
                case Constants.ACCEPTEDREQUEST -> throw new DuplicateException("Request is already accepted");
                case Constants.REJECTEDREQUEST -> throw new DuplicateException("Request is already rejected");
                default -> {
                    request.setStatus(Constants.REJECTEDREQUEST);
                    requestRepo.update(request);
                }
            }
        }
    }

    /**
     * Removes the request sent by the first user to the second user
     * @param from String the username of the user sending te request
     * @param to String the username of the user receiving the request
     * @throws NotFoundException if the request is not found
     */
    public void removeFriendRequest(String from, String to){
        Request request = requestRepo.findOne(new Tuple<>(from, to));
        if(request == null) {
            throw new NotFoundException("Request not found");
        } else {
            switch (request.getStatus()){
                case Constants.ACCEPTEDREQUEST -> throw new DuplicateException("Request is already accepted");
                case Constants.REJECTEDREQUEST -> throw new DuplicateException("Request is already rejected");
                default -> {
                    requestRepo.delete(new Tuple<>(from, to));
                }
            }
        }
    }

    /**
     * Returns a list with all the requests that can be accepted by the user
     * @param userID String the username of the user
     * @return List of Requests
     * @throws NotFoundException if the user does not exist
     */
    public List<Request> getUserFriendRequests(String userID){
        if(!userRepository.exists(userID)){
            throw new NotFoundException("User not found");
        }
        return requestRepo.getFriendRequests(userID);
    }

    public List<Request> getUserFriendRequestsPage(String username, int page){
        if(!userRepository.exists(username)){
            throw new NotFoundException("User not found");
        }
        return requestRepo.getFriendRequestsPage(username, page);
    }

    public long getUserFriendshipNumber(String username){
        return friendshipRepository.userSize(username);
    }

    public long getUserRequestNumber(String username) {
        return requestRepo.userSize(username);
    }

    public Set<Notification> getUserNotifications(String username){
        Set<Notification> notifications = new HashSet<>();
        requestRepo.findAllNotifyable(username).forEach(request -> {
            StringBuilder text = new StringBuilder();
            Notification n = new Notification(text.append(request.getFrom()).append(" sent you a friend request!").toString(), Constants.FRIENDSHIPNOTIFICATION,request.getLocalDateTime());
            notifications.add(n);
        });
        return notifications;
    }

    public int getUserFriendCount(String username){
        return friendshipRepository.getUserFriendCount(username);
    }

    public void setSeen(String username){
        requestRepo.setSeen(username);
    }
}
