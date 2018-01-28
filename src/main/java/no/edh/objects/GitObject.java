package no.edh.objects;

import no.edh.hashing.SHA1;

import java.io.*;
import java.nio.file.Path;

public interface GitObject {
    Path objectPath();
    SHA1 sha1();
    File create() throws IOException;
    Path getSourceFile();
    ObjectType objectType();
}
