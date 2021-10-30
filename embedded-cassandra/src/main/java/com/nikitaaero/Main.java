package com.nikitaaero;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.cassandra.cql3.QueryProcessor;
import org.apache.cassandra.db.ConsistencyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        final var cassandraDaemon = Utils.createCassandraDaemon(
                Files.createTempDirectory("").toString()
        );
        QueryProcessor.execute(
                        "SELECT * FROM system_schema.keyspaces",
                        ConsistencyLevel.ALL
                )
                .forEach(row -> logger.info("keyspace name: {}", row.getString("keyspace_name")));
        cassandraDaemon.deactivate();
    }

    private static URL getResourceUrl(final String resourcePath) {
        return Main.class.getClassLoader().getResource(resourcePath);
    }
}
