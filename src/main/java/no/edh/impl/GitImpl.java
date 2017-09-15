package no.edh.impl;

import no.edh.Git;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitImpl implements Git {

    private Repository repository = new Repository();

    public GitImpl() {
    }

    public GitImpl(String name) {
        this.repository = new Repository(name);
    }

    public SHA1 commit() {
        return null;
    }

    public void init(String name) throws IOException {
        repository.create(Paths.get(System.getProperty("user.dir"), name));
    }

    public void checkout(SHA1 object) {

    }

    @Override
    public void add(Path file) throws IOException {
        if (!repository.getIndex().getPath().toFile().exists()) {
            repository.getIndex().init();
        }

        File test = File.createTempFile("foo", "bar");
        RandomAccessFile f = new RandomAccessFile(test, "rw");
        f.seek(0); // to the beginning
        f.write("blob ".getBytes());
        f.write(String.format("%d", file.toFile().length()).getBytes());
        f.write(new byte[] {0});
        f.close();

        IOUtils.copy(new FileInputStream(file.toFile()), new FileOutputStream(test, true));

        repository.getIndex().addFileToIndex(test.toPath());
        repository.getObjects().addFileToObjects(test.toPath());
    }

    public static void main(String[] args) {

    }
}
