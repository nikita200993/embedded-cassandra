package com.nikitaaero.entity.mappers;

import java.util.function.Function;

import com.nikitaaero.entity.Person;
import org.apache.cassandra.cql3.UntypedResultSet;

public class UntypedRowToPersonMapper implements Function<UntypedResultSet.Row, Person> {
    @Override
    public Person apply(final UntypedResultSet.Row row) {
        return new Person(
                row.getLong("id"),
                row.getString("name"),
                row.getString("surname"),
                row.getInt("age")
        );
    }
}
