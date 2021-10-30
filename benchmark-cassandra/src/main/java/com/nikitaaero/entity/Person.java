package com.nikitaaero.entity;

public class Person {
    private final long id;
    private final String name;
    private final String surname;
    private final int age;

    public Person(
            final long id,
            final String name,
            final String surname,
            final int age
    ) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }
}
