package com.nikitaaero;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;

import org.apache.cassandra.cql3.QueryProcessor;
import org.apache.cassandra.db.ConsistencyLevel;

public class SchemaCreator {
    private final String resource;
    private final ConsistencyLevel consistencyLevel;

    public SchemaCreator(final String resource, final ConsistencyLevel consistencyLevel) {
        this.resource = resource;
        this.consistencyLevel = consistencyLevel;
    }

    public SchemaCreator(final String resource) {
        this(resource, ConsistencyLevel.ALL);
    }

    public void createSchema() {
        Utils.getResourceUrl(resource)
                .map(SchemaCreator::openStream)
                .map(SchemaCreator::readString)
                .map(string -> string.split("//"))
                .map(List::of)
                .orElseThrow()
                .forEach(statement -> QueryProcessor.execute(statement, consistencyLevel));
    }

    private static String readString(final InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static InputStream openStream(final URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
