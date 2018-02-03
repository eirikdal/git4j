package no.edh;

import no.edh.index.Index;
import no.edh.objects.Head;
import no.edh.objects.Objects;
import no.edh.refs.Refs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {

    private final Logger logger = LoggerFactory.getLogger(Repository.class);

    private Path gitInternal;
    private Refs refs;
    private Objects objects;
    private Head head;
    private final Index index;

    public Repository(String name) {
        System.setProperty("repo.dir", Paths.get(System.getProperty("user.dir"), name).toString());
        String userDir = System.getProperty("repo.dir");

        this.gitInternal = Paths.get(userDir, ".git");
        this.refs = new Refs(gitInternal.resolve("refs"));
        this.objects = new Objects(gitInternal.resolve("objects"));
        this.head = new Head(gitInternal.resolve("HEAD"));
        this.index = new Index(gitInternal.resolve("index"));
    }

    public Repository() {
        String userDir = System.getProperty("repo.dir");

        this.gitInternal = Paths.get(userDir, ".git");
        this.refs = new Refs(gitInternal.resolve("refs"));
        this.objects = new Objects(gitInternal.resolve("objects"));
        this.head = new Head(gitInternal.resolve("HEAD"));
        this.index = new Index(gitInternal.resolve("index"));
    }

    public void create(Path repository) throws IOException {
        if (!repository.resolve(".git").toFile().exists()) {
            logger.info("Initializing Git repository..");
            gitInternal = Paths.get(repository.toString(), ".git");
            gitInternal.toFile().mkdirs();
        }
        this.refs = Refs.init(gitInternal);
        this.objects = Objects.init(gitInternal);
        this.head = Head.init(gitInternal);
        logger.info("Git repository initialized...");
    }

    public Refs getRefs() {
        return refs;
    }

    public Objects getObjects() {
        return objects;
    }

    public Head getHead() {
        return head;
    }

    public Index getIndex() {
        return index;
    }
}
