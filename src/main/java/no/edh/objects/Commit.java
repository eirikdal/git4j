package no.edh.objects;

import no.edh.archive.zlib.ZlibInflater;
import no.edh.hashing.SHA1;
import no.edh.io.SideEffectWriter;
import no.edh.objects.commit.Author;
import no.edh.objects.commit.Committer;
import no.edh.objects.effects.read.CommitTreeReader;
import no.edh.objects.effects.write.CommitWrite;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Commit implements GitObject {

    private long time;
    private Author author;
    private Committer committer;
    private SHA1 tree;
    private String parent;
    private String commitMsg;

    public Commit() {

    }

    public Commit(SHA1 tree, String commitMsg) {
        this.tree = tree;
        this.commitMsg = commitMsg;
        this.time = System.currentTimeMillis();
    }

    @Override
    public Path objectPath() {
        String hash = sha1().getHashHex();

        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    @Override
    public Path realPath() {
        return null;
    }

    @Override
    public SHA1 sha1() {
        return new SHA1(this);
    }

    @Override
    public File write() throws IOException {
        File tmpFile = File.createTempFile("commit", "file");

        SideEffectWriter objectIO = new SideEffectWriter(tmpFile.toPath());
        objectIO.apply(0, Stream.of(new CommitWrite(this)));

        return tmpFile;
    }

    public static Commit read(Path path) throws IOException {
        File tmpFile = File.createTempFile("commit", "tmp");

        Commit commit = new Commit();
        ZlibInflater inflater = new ZlibInflater();
        inflater.decompressFile(path, new FileOutputStream(tmpFile));
        new SideEffectWriter(tmpFile.toPath()).apply(0, Stream.of(
                new CommitTreeReader(commit::copy)
        ));

        return commit;
    }

    private void copy(Commit commit) {
        this.author = commit.author;
        this.commitMsg = commit.commitMsg;
        this.parent = commit.parent;
        this.committer = commit.committer;
        this.time = commit.time;
        this.tree = commit.tree;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Commit;
    }

    public long getTime() {
        return time;
    }

    public Author getAuthor() {
        return author;
    }

    public Committer getCommitter() {
        return committer;
    }

    public SHA1 getTree() {
        return tree;
    }

    public String getParent() {
        return parent;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setCommitter(Committer committer) {
        this.committer = committer;
    }

    public void setTree(SHA1 tree) {
        this.tree = tree;
    }

    public void setCommitMsg(String commitMsg) {
        this.commitMsg = commitMsg;
    }
}
