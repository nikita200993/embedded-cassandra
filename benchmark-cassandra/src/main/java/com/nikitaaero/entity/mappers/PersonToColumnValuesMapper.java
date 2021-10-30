package com.nikitaaero.entity.mappers;

import java.util.function.Function;

import com.nikitaaero.entity.Person;

public class PersonToColumnValuesMapper implements Function<Person, Object[]> {
    @Override
    public Object[] apply(final Person person) {
        return new Object[]{person.getId(), person.getName(), person.getSurname(), person.getAge()};
    }
}
