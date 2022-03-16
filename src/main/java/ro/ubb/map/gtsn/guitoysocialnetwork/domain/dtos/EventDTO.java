package ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventDTO {
    String name;
    LocalDateTime dateTime;
    String description;
    Set<UserDTO> participantUsernames;
    String location;

    public EventDTO(String name, LocalDateTime dateTime, String description, Set<UserDTO> participantUsernames, String location) {
        this.name = name;
        this.dateTime = dateTime;
        this.description = description;
        this.participantUsernames = participantUsernames;
        this.location = location;
    }

    public EventDTO(String name, LocalDateTime dateTime, String description, String location) {
        this.name = name;
        this.dateTime = dateTime;
        this.description = description;
        this.participantUsernames = new HashSet<>();
        this.location = location;
    }

    public EventDTO(){
        this.name = null;
        this.dateTime = null;
        this.description = null;
        this.participantUsernames = null;
        this.location = null;
    }

    public EventDTO(Event event){
        this.name = event.getName();
        this.dateTime = event.getDateTime();
        this.description = event.getDescription();
        Set<UserDTO> userDTOS = new HashSet<>();
        event.getParticipantUsernames().forEach(username ->{
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTOS.add(userDTO);
        });
        this.participantUsernames = userDTOS;
        this.location = event.getLocation();
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

    public Set<UserDTO> getParticipantUsernames() {
        return participantUsernames;
    }

    public void setParticipantUsernames(Set<UserDTO> participantUsernames) {
        this.participantUsernames = participantUsernames;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
