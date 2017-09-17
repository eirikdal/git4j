package no.edh.objects;

import no.edh.hashing.SHA1;

import java.io.*;
import java.nio.file.Path;

public interface GitObject {
    Path objectPath() throws IOException;
    SHA1 sha1() throws IOException;
    File create() throws IOException;
    Path getSourceFile();
}
