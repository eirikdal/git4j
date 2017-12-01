package no.edh.objects;

import no.edh.hashing.SHA1;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Blob implements GitObject {

    private Path sourceFile;

    public Blob(Path sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public Path objectPath() throws IOException {
        String hash = sha1().hash();

        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    public Path getSourceFile() {
        return sourceFile;
    }

    @Override
    public SHA1 sha1() throws IOException {
        return new SHA1(this);
    }

    @Override
    public File create() throws IOException {
        File tmpFile = File.createTempFile("foo", "bar");

        RandomAccessFile f = new RandomAccessFile(tmpFile, "rw");
        f.seek(0); // to the beginning
        f.write("blob ".getBytes());
        f.write(String.format("%d", this.getSourceFile().toFile().length()).getBytes());
        f.write(new byte[]{0});
        f.close();

        try (FileInputStream input = new FileInputStream(this.getSourceFile().toFile());
             FileOutputStream output1 = new FileOutputStream(tmpFile, true)) {
            IOUtils.copy(input, output1);
        }

        return tmpFile;
    }
}
