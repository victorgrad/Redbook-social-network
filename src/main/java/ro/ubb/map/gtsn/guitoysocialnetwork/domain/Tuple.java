package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

public class Tuple<E1, E2> {
    private E1 first;
    private E2 second;

    /**
     * Class constructor
     * @param first first object in the tuple
     * @param second second object in the tuple
     */
    public Tuple(E1 first, E2 second){
        this.first = first;
        this.second = second;
    }

    /**
     * Default class constructor
     */
    public Tuple(){
        this.first = null;
        this.second = null;
    }

    /**
     * Returns the first object in the tuple
     * @return E1
     */
    public E1 getFirst() {
        return first;
    }

    /**
     * Returns the second object in the tuple
     * @return E2
     */
    public E2 getSecond() {
        return second;
    }

    /**
     * Sets first object as the parameter
     * @param first
     */
    public void setFirst(E1 first) {
        this.first = first;
    }

    /**
     * Sets second object as the parameter
     * @param second
     */
    public void setSecond(E2 second) {
        this.second = second;
    }
}
