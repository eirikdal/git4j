package no.edh.impl;

import no.edh.Git;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.objects.GitBlob;
import no.edh.objects.GitCommit;
import no.edh.objects.GitObject;
import no.edh.objects.GitTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GitImpl implements Git {

    private static final Logger logger = LoggerFactory.getLogger(GitImpl.class);
    private Repository repository = new Repository();

    public GitImpl() {
        System.setProperty("repo.dir", Paths.get(System.getProperty("user.dir")).toString());
    }

    public GitImpl(String name) {
        System.setProperty("repo.dir", Paths.get(System.getProperty("user.dir"), name).toString());
        this.repository = new Repository(name);
    }

    /**
     * Find objects staged for commit in index file
     *
     * Create a new tree with the list of objects from index file
     *
     * Make a new git commit file and store it in objects
     *
     * Move HEAD to point to the new tree
     * @param message
     * @return
     * @throws IOException
     */
    public SHA1 commit(String message) throws IOException {
        List<GitObject> objectList = repository.getIndex().findObjects();

        GitTree tree = new GitTree();
        tree.addObjects(objectList);
        this.repository.getObjects().addObject(tree);

        GitCommit commit = new GitCommit(tree, message);
        this.repository.getObjects().addObject(commit);

        try {
            repository.getObjects().addObject(commit);
        } catch (IOException e) {
            logger.error("Error writing commit", e);
        }
        repository.getHead().update(commit.sha1());
//        repository.getIndex().removeObjects();
        return new SHA1(commit);
    }

    public void init(String name) throws IOException {
        repository.create(Paths.get(System.getProperty("repo.dir"), name));
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
        repository.getIndex().addBlobToIndex(blob);
        repository.getIndex().updateIndex();
    }

    public static void main(String[] args) {

    }
}
