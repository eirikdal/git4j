package no.edh.impl;

import no.edh.Git;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.objects.GitBlob;
import no.edh.objects.GitCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitImpl implements Git {

    private static final Logger logger = LoggerFactory.getLogger(GitImpl.class);
    private Repository repository = new Repository();

    public GitImpl() {
    }

    public GitImpl(String name) {
        this.repository = new Repository(name);
    }

    public SHA1 commit(String message) {
        GitCommit commit = new GitCommit(message);

        try {
            repository.getObjects().addObject(commit);
        } catch (IOException e) {
            logger.error("Error writing commit", e);
        }
        return new SHA1(commit);
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

        GitBlob blob = new GitBlob(file);
        repository.getObjects().addObject(blob);
        repository.getIndex().addObjectToIndex(blob);
    }

    public static void main(String[] args) {

    }
}
