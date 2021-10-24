import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.cassandra.cql3.QueryProcessor;
import org.apache.cassandra.db.ConsistencyLevel;
import org.apache.cassandra.service.CassandraDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        System.setProperty(
                "cassandra.config",
                Main.class.getClassLoader().getResource("cassandra.yaml").toString()
        );
        final Path userHome = Path.of(System.getProperty("user.home"));
        System.setProperty(
                "cassandra.storagedir",
                userHome.resolve("cassandra").resolve("data").toString()
        );
        final var cassandraDaemon = CassandraDaemon.getInstanceForTesting();
        cassandraDaemon.activate();
        while (!cassandraDaemon.setupCompleted()) {
            Thread.onSpinWait();
        }
        final var version = QueryProcessor.execute(
                        "select release_version from system.local",
                        ConsistencyLevel.ALL)
                .one()
                .getString("release_version");
        Files.writeString(Path.of("version.txt"), version);
        cassandraDaemon.deactivate();
    }
}
