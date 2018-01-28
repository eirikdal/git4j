package no.edh.objects;

import no.edh.hashing.SHA1;
import no.edh.io.SideEffectWriter;
import no.edh.objects.commit.Author;
import no.edh.objects.commit.Committer;
import no.edh.objects.effects.write.CommitWrite;
import no.edh.objects.effects.write.ObjectHeadWriter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Commit implements GitObject {

    private final long time;
    private Author author;
    private Committer committer;
    private Tree tree;
    private String parent;
    private String commitMsg;

    public Commit(Tree tree, String commitMsg) {
        this.tree = tree;
        this.commitMsg = commitMsg;
        this.time = System.currentTimeMillis();
    }

    @Override
    public Path objectPath() {
        String hash = sha1().hash();

        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    @Override
    public SHA1 sha1() {
        return new SHA1(this);
    }

    @Override
    public File create() throws IOException {
        File tmpFile = File.createTempFile("commit", "file");

        SideEffectWriter objectIO = new SideEffectWriter(tmpFile.toPath());
        objectIO.apply(0, Stream.of(new CommitWrite(this)));

        return tmpFile;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public Path getSourceFile() {
        return null;
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

    public Tree getTree() {
        return tree;
    }

    public String getParent() {
        return parent;
    }

    public String getCommitMsg() {
        return commitMsg;
    }
}
