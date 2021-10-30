package com.nikitaaero.repository;

import java.util.Optional;
import java.util.function.Function;

import org.apache.cassandra.cql3.QueryProcessor;
import org.apache.cassandra.cql3.UntypedResultSet;
import org.apache.cassandra.db.ConsistencyLevel;

public class EmbeddedApiRepository<E, I> implements Repository<E, I> {

    private final String insertTemplate;
    private final String selectTemplate;
    private final ConsistencyLevel consistencyLevel;
    private final Function<E, Object[]> entityToRow;
    private final Function<UntypedResultSet.Row, E> rowToEntity;

    public EmbeddedApiRepository(
            final String insertTemplate,
            final String selectTemplate,
            final ConsistencyLevel consistencyLevel,
            final Function<E, Object[]> entityToRow,
            final Function<UntypedResultSet.Row, E> rowToEntity
    ) {
        this.insertTemplate = insertTemplate;
        this.selectTemplate = selectTemplate;
        this.consistencyLevel = consistencyLevel;
        this.entityToRow = entityToRow;
        this.rowToEntity = rowToEntity;
    }

    @Override
    public void insert(final E person) {
        QueryProcessor.execute(insertTemplate, consistencyLevel, entityToRow.apply(person));
    }

    @Override
    public Optional<E> findUnique(final I id) {
        final var resultSet = QueryProcessor.execute(selectTemplate, consistencyLevel, id);
        return resultSet.isEmpty() ? Optional.empty() : Optional.of(rowToEntity.apply(resultSet.one()));
    }
}
