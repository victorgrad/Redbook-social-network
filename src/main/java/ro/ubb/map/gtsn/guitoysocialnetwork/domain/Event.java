package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Event extends Entity<Long>{
    String name;
    LocalDateTime dateTime;
    String description;
    Set<String> participantUsernames;
    String location;

    public Event(String name, LocalDateTime dateTime, String description, String location) {
        this.name = name;
        this.dateTime = dateTime;
        this.description = description;
        this.participantUsernames = new HashSet<>();
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getParticipantUsernames() {
        return participantUsernames;
    }

    public void setParticipantUsernames(Set<String> participantUsernames) {
        this.participantUsernames = participantUsernames;
    }

    public void addParticipant(String username){
        participantUsernames.add(username);
    }

    public void removeParticipant(String username){
        participantUsernames.remove(username);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
