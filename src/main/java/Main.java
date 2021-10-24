import java.nio.file.Path;

import org.apache.cassandra.cql3.QueryProcessor;
import org.apache.cassandra.db.ConsistencyLevel;
import org.apache.cassandra.service.CassandraDaemon;

public class Main {

    public static void main(String[] args) {
        System.out.println("all okk");
        System.setProperty(
                "cassandra.config",
                Main.class.getClassLoader().getResource("cassandra.yaml").toString()
        );
        final Path userHome = Path.of(System.getProperty("user.home"));
        System.setProperty(
                "cassandra.storagedir",
                userHome.resolve("cassandra").resolve("data").toString()
        );
        final var originalStdOut = System.out;
        final var cassandraDaemon = CassandraDaemon.getInstanceForTesting();
        cassandraDaemon.activate();
        while (!cassandraDaemon.setupCompleted()) {
            Thread.onSpinWait();
        }
        originalStdOut.println("**************************************************************************************************************************");
        final var rows = QueryProcessor.execute(
                        "select release_version from system.local",
                        ConsistencyLevel.ALL)
                .one()
                .getString("release_version");
        System.out.println(rows);
    }
}
