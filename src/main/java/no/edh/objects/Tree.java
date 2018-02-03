package no.edh.objects;

import no.edh.index.ops.CacheInfo;
import no.edh.io.SideEffects;
import no.edh.objects.effects.write.TreeWrite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Tree extends GitObject {
    private List<CacheInfo> objectList;

    @Override
    public File write() throws IOException {
        File tmpFile = File.createTempFile("tree", "file");

        SideEffects objectIO = new SideEffects(tmpFile.toPath());
        objectIO.apply(Stream.of(
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
