package no.edh.objects;

import no.edh.archive.zlib.ZlibDeflater;
import no.edh.hashing.SHA1;
import org.apache.commons.codec.digest.DigestUtils;

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

    public GitBlob addObject(GitBlob blob) throws IOException {
        File objectsTmpFile = blob.getObjectsStream(blob);

        Path object = getLocationOfObject(objectsTmpFile);

        new ZlibDeflater().compress(objectsTmpFile, object.toFile());

        blob.setObjectPath(object);
        return blob;
    }

    private Path getLocationOfObject(File blob) throws IOException {
        try (FileInputStream in = new FileInputStream(blob)) {
            String hash = DigestUtils.sha1Hex(in);
            Path object = objects.resolve(hash.substring(0,2)).resolve(hash.substring(2, hash.length()));
            object.getParent().toFile().mkdir();
            return object;
        }
    }
}
