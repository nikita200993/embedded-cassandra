package com.nikitaaero.entity.repository;

import java.util.Optional;

import com.nikitaaero.entity.Person;
import com.nikitaaero.entity.mappers.PersonToColumnValuesMapper;
import com.nikitaaero.entity.mappers.UntypedRowToPersonMapper;
import com.nikitaaero.repository.EmbeddedApiRepository;
import com.nikitaaero.repository.Repository;
import org.apache.cassandra.db.ConsistencyLevel;

public class PersonEmbeddedApiRepository implements Repository<Person, Long> {

    private final EmbeddedApiRepository<Person, Long> embeddedApiRepository;

    public PersonEmbeddedApiRepository(final ConsistencyLevel consistencyLevel) {
        this.embeddedApiRepository = new EmbeddedApiRepository<>(
                "INSERT INTO test.person (id, name, surname, age) VALUES (?, ?, ?, ?)",
                "SELECT * FROM test.person WHERE id = ?",
                consistencyLevel,
                new PersonToColumnValuesMapper(),
                new UntypedRowToPersonMapper()
        );
    }

    @Override
    public void insert(final Person entity) {
        embeddedApiRepository.insert(entity);
    }

    @Override
    public Optional<Person> findUnique(final Long id) {
        return embeddedApiRepository.findUnique(id);
    }
}
