package ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyAllObservers();
}
