package no.edh.objects;

import no.edh.archive.zlib.ZlibDeflater;
import no.edh.hashing.SHA1;
import no.edh.index.entry.IndexEntry;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Objects {

    private Path objects;

    public Objects(Path objects) {
        this.objects = objects;
    }

    public static Objects init(Path repository) {
        Objects objects = new Objects(repository.resolve("objects"));
        Paths.get(repository.toString(), "objects", "info").toFile().mkdirs();
        Paths.get(repository.toString(), "objects", "pack").toFile().mkdirs();
        return objects;
    }

    public GitObject find(SHA1 sha1) throws IOException {
        String hash = sha1.hash();
        Path object = objects.resolve(hash.substring(0,2)).resolve(hash.substring(2, hash.length()));

        return new Blob(object);
    }

    public static List<GitObject> map(List<IndexEntry> entries) {
        return entries.stream()
                .map(indexEntry -> Paths.get(System.getProperty("repo.dir"), indexEntry.getObject().getSourceFile().toString()))
                .map(Blob::new)
                .collect(Collectors.toList());
    }

    public void addObject(GitObject gitObject) throws IOException {
        File objectsTmpFile = gitObject.create();
        File out = createOrGet(gitObject).toFile();

        new ZlibDeflater().compress(objectsTmpFile, out);
    }

    private Path createOrGet(GitObject gitObject) throws IOException {
        Path other = gitObject.objectPath();
        Path object = objects.resolve(other);
        if (!object.getParent().toFile().exists()) {
            object.getParent().toFile().mkdirs();
        }
        return object;
    }
}
