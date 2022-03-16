package ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Notification;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.DuplicateException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.database.EventRepo;

import java.util.HashSet;
import java.util.Set;

public class EventService {
    private final EventRepo eventRepo;

    public EventService(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    /**
     * Returns all the events that a certain user is subscribed to
     * @param username String
     * @return Set<Event>
     */
    public Set<Event> getUserEvents(String username){
        return eventRepo.findAllForUser(username);
    }

    /**
     * Returns all events that have not yet passed
     * @return Set<Event>
     */
    public Set<Event> getAllExistingEvents(){
        return eventRepo.findAllExistingEvents();
    }

    public Set<Event> getExistingEventsPage(int page){
        return eventRepo.findExistingEventsPage(page);
    }

    public long getExistingNumber(){
        return eventRepo.getExistingNumber();
    }

    /**
     * Returns wether the user is subscribed to an event or not
     * @param username String, the username of the user we are interested in
     * @param eventID Long, the id of the event
     * @return boolean
     */
    public boolean isUserSubcribedTo(String username, Long eventID){
        return eventRepo.isUserSubscribedTo(username,eventID);
    }

    public void addEvent(Event event){
        eventRepo.save(event);
    }

    public void addEventParticipant(Long eventID,String participant,Boolean toNotify){
        if(!eventRepo.isUserSubscribedTo(participant,eventID))
            eventRepo.saveParticipant(eventID,participant,toNotify);
        else
            throw new DuplicateException("You are already Subscribed to this Event");
    }

    public Event getEvent(Long id){
        return eventRepo.findOne(id);
    }

    public boolean eventExists(Long id){
        return eventRepo.exists(id);
    }

    public Set<Notification> getUserNotifications(String username){
        Set<Notification> notifications = new HashSet<>();
        eventRepo.findAllNotifiable(username).forEach(event -> {
            StringBuilder text = new StringBuilder();
            Notification n = new Notification(text.append(event.getName()).append(" is in less than 1 day!").toString(), Constants.EVENTNOTIFICATION, event.getDateTime());
            notifications.add(n);
        });
        return notifications;
    }

    public void setSeen(String username){
        eventRepo.setSeen(username);
    }


}
