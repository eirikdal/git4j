package no.edh.refs;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Refs {
    private Path refs;

    public Refs(Path refs) {
        this.refs = refs;
    }

    public static Refs init(Path gitInternal) {
        Refs refs = new Refs(Paths.get(gitInternal.toString(), "refs"));
        Paths.get(gitInternal.toString(), "refs", "heads").toFile().mkdirs();
        Paths.get(gitInternal.toString(), "refs", "tags").toFile().mkdirs();
        return refs;
    }
}
