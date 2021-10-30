package com.nikitaaero;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.cassandra.io.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraDaemonActivator {

    private static final Logger logger = LoggerFactory.getLogger(CassandraDaemonActivator.class);

    public AutoCloseable activate() {
        try {
            final var closeables = new ArrayList<AutoCloseable>();
            final Path tempDir = Files.createTempDirectory("");
            final var cassandraDaemon = Utils.createCassandraDaemon(tempDir.toString());
            closeables.add(cassandraDaemon::deactivate);
            closeables.add(() -> FileUtils.deleteRecursive(tempDir.toFile()));
            return () -> {
                for (AutoCloseable closeable : closeables) {
                    try {
                        closeable.close();
                    } catch (final Exception ex) {
                        logger.error("Failed to close resource: {}", closeable, ex);
                    }
                }
            };
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
