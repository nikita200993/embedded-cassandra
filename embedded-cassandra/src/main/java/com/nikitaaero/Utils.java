package com.nikitaaero;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.cassandra.service.CassandraDaemon;

public class Utils {

    private static final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static Optional<URL> getResourceUrl(final String resourcePath) {
        final var clazz = stackWalker.getCallerClass();
        return Optional.ofNullable(clazz.getClassLoader().getResource(resourcePath));
    }

    public static CassandraDaemon createCassandraDaemon(final String storageDir) {
        final var instance = new CassandraDaemon(true);
        System.setProperty(
                "cassandra.config",
                Utils.getResourceUrl("cassandra.yaml").orElseThrow().toString()
        );
        System.setProperty(
                "cassandra.storagedir",
                storageDir
        );
        try {
            System.setProperty(
                    "cassandra.triggers_dir",
                    Files.createDirectories(Path.of(storageDir).resolve("triggers")).toString()
            );
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
        System.setProperty(
                "cassandra-foreground",
                "true"
        );
        instance.activate();
        return instance;
    }
}
