package no.edh.refs;

import no.edh.objects.Head;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Refs {
    private Path refs;
    private final Head head;

    public Refs(Path refs) {
        this.refs = refs;
        this.head = new Head(refs.resolve("HEAD"));
    }

    public static Refs init(Path gitInternal) {
        Refs refs = new Refs(Paths.get(gitInternal.toString(), "refs"));
        Paths.get(gitInternal.toString(), "refs", "heads").toFile().mkdirs();
        Paths.get(gitInternal.toString(), "refs", "tags").toFile().mkdirs();
        return refs;
    }
}
