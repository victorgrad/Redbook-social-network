package ro.ubb.map.gtsn.guitoysocialnetwork.domain.models;

import javafx.scene.image.ImageView;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;

import java.time.LocalDateTime;

public class EventModel {
    Long id;
    private String name;
    private LocalDateTime date;
    private String description;
    private String location;
    private ImageView status;

    public EventModel(Long id,String name, LocalDateTime date, String description, String location, ImageView status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.location = location;
        this.status = status;
    }

    public EventModel(){
        this.id = null;
        this.name = null;
        this.date = null;
        this.description = null;
        this.location = null;
        this.status = null;
    }

    public EventModel(Event event){
        this.id = event.getId();
        this.name = event.getName();
        this.date = event.getDateTime();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.status = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date.format(Constants.DATE_TIME_FORMATTER2);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ImageView getStatus() {
        return status;
    }

    public void setStatus(ImageView status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
