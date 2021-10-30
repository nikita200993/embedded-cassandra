package com.nikitaaero.entity.repository;

import java.util.Optional;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.nikitaaero.entity.Person;
import com.nikitaaero.entity.mappers.PersonToColumnValuesMapper;
import com.nikitaaero.entity.mappers.RowToPersonMapper;
import com.nikitaaero.repository.DriverBasedRepository;
import com.nikitaaero.repository.Repository;

public class PersonDriverBasedRepo implements Repository<Person, Long> {

    private final DriverBasedRepository<Person, Long> driverBasedRepository;

    public PersonDriverBasedRepo(final DriverBasedRepository<Person, Long> driverBasedRepository) {
        this.driverBasedRepository = driverBasedRepository;
    }

    public static PersonDriverBasedRepo create(final CqlSession session) {
        final PreparedStatement insertStatement = session.prepare(
                "INSERT INTO test.person (id, name, surname, age) VALUES (?, ?, ?, ?)"
        );
        final PreparedStatement selectStatement = session.prepare("SELECT * FROM test.person WHERE id = ?");
        return new PersonDriverBasedRepo(
                new DriverBasedRepository<>(
                        session,
                        insertStatement,
                        selectStatement,
                        new PersonToColumnValuesMapper(),
                        new RowToPersonMapper()
                )
        );

    }

    @Override
    public void insert(final Person entity) {
        driverBasedRepository.insert(entity);
    }

    @Override
    public Optional<Person> findUnique(final Long id) {
        return driverBasedRepository.findUnique(id);
    }
}
