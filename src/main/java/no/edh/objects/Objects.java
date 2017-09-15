package no.edh.objects;

import no.edh.archive.zlib.ZlibInflater;
import no.edh.hashing.SHA1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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

    public Path addFileToObjects(Path file) throws IOException {
        String hash = new SHA1(new GitBlob(file)).hash();
        Path object = objects.resolve(hash.substring(0,2)).resolve(hash.substring(2, hash.length()));
        object.getParent().toFile().mkdir();
        new ZlibInflater().compress(new FileInputStream(file.toFile()), new FileOutputStream(object.toFile()));
        return object;
    }
}
