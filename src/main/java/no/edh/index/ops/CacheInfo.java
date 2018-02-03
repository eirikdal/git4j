package no.edh.index.ops;

import no.edh.hashing.SHA1;

import java.nio.file.Path;

public class CacheInfo {

    private final FileMode fileMode;
    private final SHA1 hash;
    private final Path path;

    public CacheInfo(FileMode fileMode, SHA1 hash, Path path) {
        this.fileMode = fileMode;
        this.hash = hash;
        this.path = path;
    }

    public FileMode getFileMode() {
        return fileMode;
    }

    public SHA1 getHash() {
        return hash;
    }

    public Path getPath() {
        return path;
    }
}
