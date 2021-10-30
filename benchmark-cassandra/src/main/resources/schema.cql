CREATE KEYSPACE IF NOT EXISTS test
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
        };
//
DROP TABLE IF EXISTS test.person;
//
CREATE TABLE test.person
(
    id      bigint PRIMARY KEY,
    name    varchar,
    surname varchar,
    age     int
)
