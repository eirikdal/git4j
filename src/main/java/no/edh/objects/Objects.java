package no.edh.objects;

import no.edh.archive.zlib.ZlibDeflater;
import no.edh.hashing.SHA1;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        return new GitBlob(object);
    }

    public void addObject(GitObject gitObject) throws IOException {
        File objectsTmpFile = gitObject.create();

        Path other = gitObject.objectPath();
        Path object = objects.resolve(other);
        if (!object.getParent().toFile().exists()) {
            object.getParent().toFile().mkdirs();
        }

        new ZlibDeflater().compress(objectsTmpFile, object.toFile());
    }
}
