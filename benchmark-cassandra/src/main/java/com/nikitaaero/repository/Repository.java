package com.nikitaaero.repository;

import java.util.Optional;

public interface Repository<E, I> {
    void insert(E entity);
    Optional<E> findUnique(I id);
}
