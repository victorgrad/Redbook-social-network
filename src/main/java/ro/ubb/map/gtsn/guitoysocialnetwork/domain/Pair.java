package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import java.util.Objects;

public class Pair<E extends Comparable<E>>{
    private final E e1;
    private final E e2;

    /**
     * Class constructor
     * @param e1 the first object
     * @param e2 the second object
     */
    public Pair(E e1, E e2) {
        if(e1.compareTo(e2) < 0) {
            this.e1 = e1;
            this.e2 = e2;
        }else{
            this.e1 = e2;
            this.e2 = e1;
        }
    }

    /**
     * Returns the smaller object
     * @return E
     */
    public E getLeft() {
        return e1;
    }

    /**
     * return the bigger object
     * @return E
     */
    public E getRight() {
        return e2;
    }

    /**
     * Returns a string with information about the current instance
     * @return String
     */
    @Override
    public String toString() {
        return "(" + e1 + "," + e2 + ")";

    }

    /**
     * Defines the equality between two instances
     * @param obj the object to which is compared the current instance
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof Pair<?> cp)){
            return false;
        }
        return this.e1.equals(cp.e1) && this.e2.equals(cp.e2);
    }

    /**
     * Returns the hash code of the current instance
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}
