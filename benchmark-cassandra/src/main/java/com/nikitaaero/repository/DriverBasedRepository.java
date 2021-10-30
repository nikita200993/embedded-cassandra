package com.nikitaaero.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;

public class DriverBasedRepository<E, I> implements Repository<E, I> {
    private final CqlSession session;
    private final PreparedStatement insertTemplate;
    private final PreparedStatement selectTemplate;
    private final Function<E, Object[]> entityToRow;
    private final Function<Row, E> rowToEntity;

    public DriverBasedRepository(
            final CqlSession session,
            final PreparedStatement insertTemplate,
            final PreparedStatement selectTemplate,
            final Function<E, Object[]> entityToRow,
            final Function<Row, E> rowToEntity
    ) {
        this.session = session;
        this.insertTemplate = insertTemplate;
        this.selectTemplate = selectTemplate;
        this.entityToRow = entityToRow;
        this.rowToEntity = rowToEntity;
    }


    @Override
    public void insert(final E entity) {
        session.execute(insertTemplate.bind(entityToRow.apply(entity)));
    }

    @Override
    public Optional<E> findUnique(final I id) {
        final var resultSet = session.execute(selectTemplate.bind(id));
        final List<Row> rows = new ArrayList<>();
        for (Row row : resultSet) {
            rows.add(row);
            if (rows.size() > 1) {
                throw new RuntimeException("more than one row returned for id " + id);
            }
        }
        return rows.isEmpty() ? Optional.empty() : Optional.of(rowToEntity.apply(rows.get(0)));
    }
}
