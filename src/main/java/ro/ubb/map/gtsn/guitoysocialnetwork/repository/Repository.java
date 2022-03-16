package ro.ubb.map.gtsn.guitoysocialnetwork.repository;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Entity;

public interface Repository<ID, E extends Entity<ID>> {
    E findOne(ID id);
    Iterable<E> findAll();
    ID save(E entity);
    void delete(ID id);
    void update(E entity);
    boolean exists(ID id);
}
