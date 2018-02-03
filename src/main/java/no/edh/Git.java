package no.edh;

import com.jcraft.jsch.IO;
import no.edh.hashing.SHA1;
import no.edh.index.entry.IndexEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Git {

    SHA1 commit(String message) throws IOException;
    void init(String name) throws IOException;
    void checkout(SHA1 object);
    SHA1 add(Path file) throws IOException;
    void remove(Path file) throws IOException;
    List<IndexEntry> status();
}
