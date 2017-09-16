package no.edh.objects;

import java.io.*;
import java.nio.file.Path;

public interface GitObject {
    Path getPath();
    InputStream getHashStream() throws IOException;
    InputStream getContentStream();
}
