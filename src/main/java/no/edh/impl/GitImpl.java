package no.edh.impl;

import no.edh.Git;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.index.entry.IndexEntry;
import no.edh.objects.GitBlob;
import no.edh.objects.GitCommit;
import no.edh.objects.GitObject;
import no.edh.objects.GitTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<GitObject> objects = map(entries);

        GitTree tree = new GitTree();
        tree.addObjects(objects);
        repository.getObjects().addObject(tree);

        GitCommit commit = new GitCommit(tree, message);
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
        GitBlob blob = new GitBlob(file);
        blob.create();

        repository.getObjects().addObject(blob);
        List<IndexEntry> entries = repository.getIndex().readEntries();
        repository.getIndex().removeEntries();
        List<GitObject> objects = map(entries);
        objects.add(blob);
        objects.sort(Comparator.comparing(o -> o.getSourceFile().toFile().getName()));
        objects.forEach(gitObject -> repository.getIndex().addBlobToIndex(gitObject));
        repository.getIndex().updateIndex();
    }

    private List<GitObject> map(List<IndexEntry> entries) {
        return entries.stream()
                .map(indexEntry -> Paths.get(System.getProperty("repo.dir"), indexEntry.getObject().getSourceFile().toString()))
                .map(GitBlob::new)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {

    }
}
