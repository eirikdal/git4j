package no.edh.objects;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitBlob implements GitObject, Closeable {

    private Path workingFilePath;
    private InputStream inputStream;
    private Path objectPath;

    public GitBlob(Path workingFilePath) {
        this.workingFilePath = workingFilePath;
    }

    @Override
    public Path getObjectPath() {
        return objectPath;
    }

    public Path getWorkingFilePath() {
        return workingFilePath;
    }

    public InputStream getHashStream() throws IOException {
        inputStream = Files.newInputStream(workingFilePath);
        return inputStream;
    }

    public File getObjectsStream(GitBlob blob) throws IOException {
        File tmpFile = File.createTempFile("foo", "bar");

        RandomAccessFile f = new RandomAccessFile(tmpFile, "rw");
        f.seek(0); // to the beginning
        f.write("blob ".getBytes());
        f.write(String.format("%d", blob.getWorkingFilePath().toFile().length()).getBytes());
        f.write(new byte[]{0});
        f.close();

        try (FileInputStream input = new FileInputStream(blob.getWorkingFilePath().toFile());
             FileOutputStream output1 = new FileOutputStream(tmpFile, true)) {
            IOUtils.copy(input, output1);
        }

        return tmpFile;
    }
    public InputStream getContentStream() {
        return inputStream;
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public void setObjectPath(Path objectPath) {
        this.objectPath = objectPath;
    }
}
