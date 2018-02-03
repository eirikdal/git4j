package no.edh.objects;

import no.edh.io.SideEffects;
import no.edh.objects.effects.write.BlobWrite;
import no.edh.objects.effects.write.BlobHeadWrite;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Blob extends GitObject {

    private Path sourceFile;

    public Blob(Path sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Path realPath() {
        return sourceFile;
    }

    @Override
    public File write() throws IOException {
        File tmpFile = File.createTempFile("blob", "file");

        SideEffects objectIO = new SideEffects(tmpFile.toPath());
        objectIO.apply(0, Stream.of(
                new BlobHeadWrite(this),
                new BlobWrite(this)
        ));

        return tmpFile;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Blob;
    }
}
