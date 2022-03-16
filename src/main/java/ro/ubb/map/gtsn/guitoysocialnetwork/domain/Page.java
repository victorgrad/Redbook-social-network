package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos.UserDTO;

import java.util.List;

public class Page {
    final UserDTO user;
    final List<Notification> notifications;
    final int friendCount;
    final int newMessageCount;
    final int newRequestCount;

    public Page(UserDTO user, List<Notification> notifications, int friendCount, int newMessageCount, int newRequestCount) {
        this.user = user;
        this.notifications = notifications;
        this.friendCount = friendCount;
        this.newMessageCount = newMessageCount;
        this.newRequestCount = newRequestCount;
    }

    public UserDTO getUser() {
        return user;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public int getNewRequestCount() {
        return newRequestCount;
    }
}
