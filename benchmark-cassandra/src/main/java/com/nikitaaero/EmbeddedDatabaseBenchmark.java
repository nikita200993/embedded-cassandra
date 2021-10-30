package com.nikitaaero;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.nikitaaero.entity.Person;
import com.nikitaaero.entity.repository.PersonDriverBasedRepo;
import com.nikitaaero.entity.repository.PersonEmbeddedApiRepository;
import com.nikitaaero.repository.Repository;
import org.apache.cassandra.cql3.ColumnIdentifier;
import org.apache.cassandra.cql3.QueryProcessor;
import org.apache.cassandra.cql3.UntypedResultSet;
import org.apache.cassandra.db.ConsistencyLevel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BenchmarkMode(Mode.Throughput)
@Fork(value = 1, jvmArgsPrepend = {"--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED"})
@Measurement(iterations = 1)
@Warmup(iterations = 2)
@State(Scope.Thread)
public class EmbeddedDatabaseBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedDatabaseBenchmark.class);

    private final Random random = new Random();
    private final int numRows = 10_000_000;
    private final Repository<Person, Long> repo = new PersonEmbeddedApiRepository(ConsistencyLevel.ONE);

    private CqlSession session;
    private Repository<Person, Long> driverBasedRepo;
    private AutoCloseable resource;
    private List<Long> ids;

    @Setup
    public void prepare() {
        activateDaemon();
        createSchema();
        createSession();
        driverBasedRepo = PersonDriverBasedRepo.create(session);
        insertRows();
    }

    private void createSchema() {
        logger.info("Creating schema.");
        new SchemaCreator("schema.cql").createSchema();
        logger.info("Schema was created.");
        showRows("SELECT table_name FROM system_schema.tables WHERE keyspace_name = 'test'");
    }

    private void createSession() {
        logger.info("Creating session...");
        session = new CqlSessionBuilder()
                .build();
        logger.info("Session created.");
    }

    @TearDown
    public void close() throws Exception {
        logger.info("Closing...");
        session.close();
        resource.close();
    }

    @Setup(Level.Invocation)
    public void setIds() {
        ids = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ids.add((long) random.nextInt(numRows));
        }
    }

    @Benchmark
    @OperationsPerInvocation(1000)
    public void selectViaEmbeddedApi(final Blackhole blackhole) {
        for (final Long id : ids) {
            blackhole.consume(repo.findUnique(id));
        }
    }

    @Benchmark
    @OperationsPerInvocation(1000)
    public void selectViaDriverApi(final Blackhole blackhole) {
        for (final Long id : ids) {
            blackhole.consume(driverBasedRepo.findUnique(id));
        }
    }

    private void showRows(final String s) {
        logRows(QueryProcessor.execute(s, ConsistencyLevel.ALL));
    }

    private void activateDaemon() {
        logger.info("Activating cassandra daemon...");
        resource = new CassandraDaemonActivator().activate();
        logger.info("Cassandra daemon was activated.");
    }

    private void insertRows() {
        logger.info("Inserting rows...");
        for (int i = 0; i < numRows; i++) {
            repo.insert(randomPerson(i));
            final int percent = insertionProgressPercent(i + 1);
            final int previousPercent = insertionProgressPercent(i);
            if (percent % 10 == 0 && percent != previousPercent) {
                logger.info("Insertion progress {}%.", percent);
            }
        }
        logger.info("Rows were inserted.");
    }

    private static void logRows(final UntypedResultSet resultSet) {
        final var columnsMetaData = resultSet.metadata();
        final var header = columnsMetaData.stream()
                .map(columnMeta -> columnMeta.name)
                .map(ColumnIdentifier::toString)
                .collect(Collectors.joining(","));
        logger.info(
                "\n*** header ***\n{}\n*** rows ***\n{}",
                header,
                StreamSupport.stream(resultSet.spliterator(), false)
                        .map(EmbeddedDatabaseBenchmark::toList)
                        .map(List::stream)
                        .map(stringStream -> stringStream.collect(Collectors.joining(",")))
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    private static List<String> toList(final UntypedResultSet.Row row) {
        final var columnsMetaData = row.getColumns();
        return columnsMetaData.stream()
                .map(columnMetaData -> row.getString(columnMetaData.name.toString()))
                .collect(Collectors.toList());
    }

    private Person randomPerson(final int id) {
        return new Person(
                id,
                String.valueOf(random.nextInt(10_000)),
                String.valueOf(random.nextInt(10_000)),
                random.nextInt(50) + 18
        );
    }

    private int insertionProgressPercent(final int rowsInserted) {
        return (100 * rowsInserted / numRows);
    }
}

