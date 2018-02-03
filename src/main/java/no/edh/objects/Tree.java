package no.edh.objects;

import no.edh.hashing.SHA1;
import no.edh.index.ops.CacheInfo;
import no.edh.io.SideEffectWriter;
import no.edh.objects.effects.write.TreeWrite;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Tree implements GitObject {
    private List<CacheInfo> objectList;

    @Override
    public Path objectPath() {
        String hash = sha1().getHashHex();

        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    @Override
    public SHA1 sha1() {
        return new SHA1(this);
    }

    @Override
    public File write() throws IOException {
        File tmpFile = File.createTempFile("tree", "file");

        SideEffectWriter objectIO = new SideEffectWriter(tmpFile.toPath());
        objectIO.apply(0, Stream.of(
                new TreeWrite(this)
        ));

        return tmpFile;
    }

    @Override
    public Path realPath() {
        return null;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tree;
    }

    public void addObjects(List<CacheInfo> objectList) {
        this.objectList = objectList;
    }

    public List<CacheInfo> getObjectList() {
        return objectList;
    }
}
