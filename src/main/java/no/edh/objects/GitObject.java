package no.edh.objects;

import no.edh.hashing.SHA1;

import java.io.*;
import java.nio.file.Path;

public interface GitObject {
    Path objectPath();
    Path realPath();
    SHA1 sha1();
    File write() throws IOException;
    ObjectType objectType();
}
