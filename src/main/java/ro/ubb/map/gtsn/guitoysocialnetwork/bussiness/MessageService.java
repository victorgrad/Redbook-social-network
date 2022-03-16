package ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Notification;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.NotFoundException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.MessageRepo;

import java.util.HashSet;
import java.util.Set;

public class MessageService {
    private final MessageRepo messageRepository;

    /**
     * Class constructor
     * @param messageRepo - instance of MessageRepo
     */
    public MessageService(MessageRepo messageRepo) {
        this.messageRepository = messageRepo;
    }

    /**
     * Adds the message in the repository
     * @param message - a valid message object
     */
    public Long sendMessage(Message message){
        return messageRepository.save(message);
    }

    /**
     * Returns the Message object for the Message id provided
     * @param messageID - a Long
     * @return an instance of Message
     * @throws NotFoundException - if the message does not exist
     */
    public Message getMessage(Long messageID){
        Message found = messageRepository.findOne(messageID);
        if(found == null){
            throw new NotFoundException("Message not found");
        }
        return found;
    }

    /**
     * Returns an iterable container that contains all the messages
     * @return - an iterable container
     */
    public Set<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    /**
     * Returns the specified page of messages
     * @param page int
     * @return an iterable container
     * @throws ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException if page < 0
     */
    public Set<Message> getMessagePage(int page){
        return messageRepository.findPage(page);
    }

    /**
     * Returns the set of messages that were sent or received by a user
     * @param userId String, the id of the user
     * @return Set<Message>
     */
    public Set<Message> getUserMessages(String userId){
        return messageRepository.findUserMessages(userId);
    }

    public Set<Message> getUserMessagesPage(String userId, int page){
        return messageRepository.findUserMessagesPage(userId, page);
    }

    public Set<Message> getLastUserMessagesFromChainPage(String userID, int page){
        return messageRepository.getLastUserMessagesFromChainPage(userID, page);
    }
    /**
     * Returns the set of messages that make up the conversation
     * @param userId1 String, the id of the first user
     * @param userId2 String,  the id of the second user
     * @return Set<Message>
     */
    public Set<Message> getConversation(String userId1, String userId2){
        return messageRepository.findConversation(userId1,userId2);
    }

    public Set<Message> getConversationPage(String user1, String user2, int page){
        return messageRepository.findConversationPage(user1, user2, page);
    }

    public long getUserMessageNumber(String username) {
        return messageRepository.userSize(username);
    }

    public Set<Notification> getUserNotifications(String username){
        Set<Notification> notifications = new HashSet<>();
        messageRepository.findAllNotifyable(username).forEach(message -> {
            StringBuilder text = new StringBuilder();
            Notification n = new Notification(text.append("New message from ").append(message.getFrom()).toString(), Constants.MESSAGENOTIFICATION,message.getMessageDateTime());
            notifications.add(n);
        });
        return notifications;
    }

    public void setSeen(String username){
        messageRepository.setSeen(username);
    }
}
