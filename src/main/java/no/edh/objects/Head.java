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

    public SHA1 getHead() {
        return head;
    }

    public static Head init(Path repository) throws IOException {
        Head head = new Head(repository);
        Files.write(Paths.get(repository.toString(), "HEAD"), "ref: refs/heads/master".getBytes());
        return head;
    }
}