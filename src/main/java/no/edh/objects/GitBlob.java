package no.edh.objects;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitBlob implements GitObject, Closeable {

    private Path blob;
    private InputStream inputStream;

    public GitBlob(Path blob) {
        this.blob = blob;
    }

    @Override
    public Path getPath() {
        return blob;
    }

    public InputStream getHashStream() throws IOException {
        inputStream = Files.newInputStream(blob);
        return inputStream;
    }

    public OutputStream stream() throws IOException {
        File output = File.createTempFile("foo", "bar");

        RandomAccessFile f = new RandomAccessFile(output, "rw");
        f.seek(0); // to the beginning
        f.write("blob ".getBytes());
        f.write(String.format("%d", blob.toFile().length()).getBytes());
        f.write(new byte[] {0});
        f.close();

        FileOutputStream fos = new FileOutputStream(output, true);
        IOUtils.copy(new FileInputStream(blob.toFile()), fos);
        return fos;
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
