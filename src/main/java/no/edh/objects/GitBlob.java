package no.edh.objects;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitBlob implements GitObject, Closeable {

    private Path blob;
    private InputStream inputStream;

    public GitBlob(Path blob) {
        this.blob = blob;
    }

    public InputStream getHashStream() throws IOException {
        inputStream = Files.newInputStream(blob);
        return inputStream;
    }

    public InputStream getContentStream() {
        return inputStream;
    }

    public Path getLocation() {
        return blob;
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
