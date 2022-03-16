package ro.ubb.map.gtsn.guitoysocialnetwork.domain.models;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.RequestDTO;

public class RequestModel {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String status;
    private final String date;

    public RequestModel(RequestDTO requestDTO) {
        this.username = requestDTO.getFrom().getUsername();
        this.firstName = requestDTO.getFrom().getFirstname();
        this.lastName = requestDTO.getFrom().getLastname();
        this.status = requestDTO.getStatus();
        this.date = requestDTO.getLocalDateTime().format(Constants.DATE_TIME_FORMATTER);
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
