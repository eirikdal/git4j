package no.edh.objects;

import no.edh.hashing.SHA1;

import java.io.*;
import java.nio.file.Path;

public abstract class GitObject {
    public abstract Path realPath();
    public abstract File write() throws IOException;
    public abstract ObjectType objectType();

    public Path objectPath() {
        return Objects.find(sha1().getHashHex());
    }

    public SHA1 sha1() {
        return new SHA1(this);
    }
}
