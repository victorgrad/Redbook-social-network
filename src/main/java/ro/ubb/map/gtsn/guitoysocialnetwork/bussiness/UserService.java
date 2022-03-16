package ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.FriendshipRepo;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.UserRepo;

import java.util.List;

public class UserService {
    private final UserRepo userRepository;
    private final FriendshipRepo friendshipRepository;

    /**
     * Class constructor
     * @param userRepository - instance of UserRepository
     */
    public UserService(UserRepo userRepository, FriendshipRepo friendshipRepository){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Checks if the username and the password are correct
     * @param userDTO a DTO that contains the username and password hash
     * @return true if the credentials are correct, false otherwise
     */
    public boolean checkPassword(UserDTO userDTO){
        String userId = userDTO.getUsername();
        String passwordHash = userDTO.getPasswordHash();

        if(!userRepository.exists(userId)){
            return false;
        }

        String hash = userRepository.getPasswordHash(userId);

        return passwordHash.equals(hash);
    }

    /**
     * Returns the user with the same id as userID
     * @param userID - a user id
     * @return the user with the id userID
     * @throws NotFoundException if the user doesn't exist
     */
    public User getUser(String userID){
        User found =  userRepository.findOne(userID);
        if(found == null){
            throw new NotFoundException("User not found");
        }
        return found;
    }

    /**
     * Adds the user user in repository
     * @param user - a valid user
     * @throws DuplicateException - if a user with the same id already exists in repository
     */
    public void addUser(User user){
        if(userRepository.exists(user.getId())){
            throw new DuplicateException("User already exists");
        }
        userRepository.save(user);
    }

    /**
     * removes from repository the user with the id userID
     * @param userID - the id of the user to be removed
     * @throws NotFoundException if the user doesn't exist
     */
    public void removeUser(String userID){
        if(!userRepository.exists(userID)){
            throw new NotFoundException("User not found");
        }
        userRepository.delete(userID);
    }

    /**
     * returns an iterable container with all the users in the repository
     * @return - an iterable container
     */
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * returns the specified page of users
     * @param page int
     * @return iterable of users
     * @throws ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException if page < 0
     */
    public Iterable<User> getUsersPage(int page){
        return userRepository.findPage(page);
    }

    public void updateUser(User user){
        if(!userRepository.exists(user.getId())){
            throw new NotFoundException("User not found");
        }
        userRepository.update(user);
    }

    public boolean userExists(String username){
        return userRepository.exists(username);
    }

    public List<User> getUserPageByName(String filter, int page){
        return userRepository.findUserPageByName(filter, page);
    }

    public long getSize(){
        return userRepository.size();
    }
}
