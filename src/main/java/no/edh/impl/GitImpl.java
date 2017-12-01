package no.edh.impl;

import no.edh.Git;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.index.entry.IndexEntry;
import no.edh.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

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
     * Create a new tree with the list of objects from index file. Store the new tree object in objects.
     *
     * Make a new git commit object and store it in objects. Update parent if necessary.
     *
     * Move HEAD to point to the new commit
     * @param message
     * @return
     * @throws IOException
     */
    public SHA1 commit(String message) throws IOException {
        List<IndexEntry> entries = repository.getIndex().readEntries();
        List<GitObject> objects = Objects.map(entries);

        Tree tree = new Tree();
        tree.addObjects(objects);
        repository.getObjects().addObject(tree);

        Commit commit = new Commit(tree, message);
        if (repository.getHead().exists()) {
            commit.setParent(repository.getHead().getHead());
        }
        repository.getObjects().addObject(commit);
        repository.getHead().update(commit.sha1());
        return new SHA1(commit);
    }

    public void init() throws IOException {
        repository.create(Paths.get(System.getProperty("repo.dir")));
        repository.getIndex().init();
    }

    public void init(String name) throws IOException {
        repository.create(Paths.get(System.getProperty("repo.dir"), name));
    }

    /**
     *  TODO:
     *
     *  Make sure staging area is clean
     *
     * Create a new ref for the branch if necessary (if so, set sha1 of ref to current branch unless otherwise specified)
     *
     * Update HEAD to point to the other ref (branch)
     *
     * Sanitize working area (difficult part) // otherwise we can treat this as a soft reset
     *
     * @param object
     */
    public void checkout(SHA1 object) {

    }

    @Override
    public void add(Path file) throws IOException {
        Blob blob = new Blob(file);
        blob.create();

        repository.getObjects().addObject(blob);
        List<IndexEntry> entries = repository.getIndex().readEntries();
        repository.getIndex().removeEntries();

        List<GitObject> objects = Objects.map(entries);
        objects.add(blob);
        objects.sort(comparing(o -> o.getSourceFile().toFile().getName()));
        objects.forEach(gitObject -> repository.getIndex().addBlobToIndex(gitObject));
        repository.getIndex().updateIndex();
    }

    public static void main(String[] args) {

    }
}
