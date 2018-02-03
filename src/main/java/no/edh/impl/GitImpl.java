package no.edh.impl;

import no.edh.Git;
import no.edh.Plumbing;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.index.entry.IndexEntry;
import no.edh.index.ops.CacheInfo;
import no.edh.index.ops.FileMode;
import no.edh.index.ops.UpdateIndex;
import no.edh.objects.Blob;
import no.edh.objects.Commit;
import no.edh.objects.Objects;
import no.edh.objects.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GitImpl implements Git {

    private static final Logger logger = LoggerFactory.getLogger(GitImpl.class);
    private Repository repository = new Repository();
    private Plumbing plumbing;

    public GitImpl() {
        System.setProperty("repo.dir", Paths.get(System.getProperty("user.dir")).toString());
    }

    public GitImpl(String name) {
        System.setProperty("repo.dir", Paths.get(System.getProperty("user.dir"), name).toString());
        this.repository = new Repository(name);
        this.plumbing = new Plumbing(repository);
    }

    public GitImpl(Path repo) {
        System.setProperty("repo.dir", repo.toString());
        this.repository = new Repository();
        this.plumbing = new Plumbing(repository);
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
        List<CacheInfo> objects = Objects.map(entries);

        Tree tree = new Tree();
        tree.addObjects(objects);
        repository.getObjects().writeObject(tree);

        Commit commit = new Commit(tree.sha1(), message);
        if (repository.getHead().exists()) {
            commit.setParent(repository.getHead().getHead());
        }
        repository.getObjects().writeObject(commit);
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
    public SHA1 add(Path file) throws IOException {
        Blob blob = new Blob(file);

        SHA1 sha1 = plumbing.hashObject(blob, true);
        plumbing.updateIndex(UpdateIndex.ADD, new CacheInfo(FileMode.Blob, sha1, blob.realPath()));
        return sha1;
    }

    @Override
    public void remove(Path file) throws IOException {
        Blob blob = new Blob(file);

        SHA1 sha1 = plumbing.hashObject(blob, true);
        plumbing.updateIndex(UpdateIndex.ADD, new CacheInfo(FileMode.Blob, sha1, blob.realPath()));
    }

    @Override
    public List<IndexEntry> status() {
        return repository.getIndex().readEntries();
    }

    public static void main(String[] args) {

    }
}
