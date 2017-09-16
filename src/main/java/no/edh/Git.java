package no.edh;

import no.edh.hashing.SHA1;

import java.io.IOException;
import java.nio.file.Path;

public interface Git {

    SHA1 commit(String message);
    void init(String name) throws IOException;
    void checkout(SHA1 object);
    void add(Path file) throws IOException;
}
