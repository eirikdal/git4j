package no.edh.objects;

import no.edh.io.SideEffects;
import no.edh.objects.effects.write.BlobHeadWrite;
import no.edh.objects.effects.write.BlobWrite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Blob extends GitObject {

    private final Path sourceFile;

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
        objectIO.apply(Stream.of(
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
