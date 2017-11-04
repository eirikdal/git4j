package no.edh.objects;

import no.edh.hashing.SHA1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Head {
    private SHA1 head;
    private Path repository;

    public Head(Path repository) {
        this.repository = repository;
    }

    public void move(SHA1 to) {
        this.head = to;
    }

    public String getHead() throws IOException {
        Path path = Paths.get(repository.toString(), "refs", "heads", "master");
        return Files.readAllLines(path).get(0); // hardcoded to master for now..
    }

    public boolean exists() {
        return Paths.get(repository.toString(), "refs", "heads", "master").toFile().exists();
    }

    public void update(SHA1 sha1) throws IOException {
        Path master = Paths.get(repository.toString(), "refs", "heads", "master");
        if (!master.getParent().toFile().exists()) {
            master.getParent().toFile().mkdirs();
            master.toFile().createNewFile();
        }
        Files.write(master, sha1.hash().getBytes());
    }

    public static Head init(Path repository) throws IOException {
        Head head = new Head(repository);
        Files.write(Paths.get(repository.toString(), "HEAD"), "ref: refs/heads/master".getBytes());
        return head;
    }
}
