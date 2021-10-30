package com.nikitaaero;

import java.net.URL;
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
        System.setProperty(
                "cassandra-foreground",
                "true"
        );
        instance.activate();
        return instance;
    }
}
