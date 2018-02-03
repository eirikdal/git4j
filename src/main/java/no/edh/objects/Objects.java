package no.edh.objects;

import no.edh.index.entry.IndexEntry;
import no.edh.index.ops.CacheInfo;
import no.edh.index.ops.FileMode;
import no.edh.zlib.ZlibDeflater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class Objects {

    private final Path objects;

    public Objects(Path objects) {
        this.objects = objects;
    }

    public static Objects init(Path repository) {
        Objects objects = new Objects(repository.resolve("objects"));
        Paths.get(repository.toString(), "objects", "info").toFile().mkdirs();
        Paths.get(repository.toString(), "objects", "pack").toFile().mkdirs();
        return objects;
    }

    public static Path find(String hash) {
        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    public static Path absolutize(String hash) {
        Path repo = Paths.get(System.getProperty("repo.dir"), ".git", "objects");

        return repo.resolve(find(hash));
    }

    public static List<CacheInfo> map(List<IndexEntry> entries) {
        return entries.stream()
                .map(indexEntry -> Paths.get(System.getProperty("repo.dir"), indexEntry.getPath().toString()))
                .map((Path path) -> new CacheInfo(FileMode.Blob, new Blob(path).sha1(), path))
                .collect(Collectors.toList());
    }

    public void writeObject(GitObject gitObject) throws IOException {
        File objectsTmpFile = gitObject.write();
        File out = createOrGet(gitObject).toFile();

        new ZlibDeflater().compress(objectsTmpFile, out);
    }

    private Path createOrGet(GitObject gitObject) {
        Path other = gitObject.objectPath();
        Path object = objects.resolve(other);
        if (!object.getParent().toFile().exists()) {
            object.getParent().toFile().mkdirs();
        }
        return object;
    }

    public static void sort(List<CacheInfo> objects) {
        objects.sort(comparing(o -> o.getPath().toFile().getName()));
    }
}
