package com.nikitaaero.entity.mappers;

import java.util.function.Function;

import com.datastax.oss.driver.api.core.cql.Row;
import com.nikitaaero.entity.Person;

public class RowToPersonMapper implements Function<Row, Person> {
    @Override
    public Person apply(final Row row) {
        return new Person(
                row.getLong("id"),
                row.getString("name"),
                row.getString("surname"),
                row.getInt("age")
        );
    }
}
