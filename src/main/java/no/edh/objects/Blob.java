package no.edh.objects;

import no.edh.hashing.SHA1;
import no.edh.io.SideEffectWriter;
import no.edh.objects.effects.write.BlobWrite;
import no.edh.objects.effects.write.ObjectHeadWriter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Blob implements GitObject {

    private Path sourceFile;

    public Blob(Path sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public Path objectPath() {
        String hash = sha1().hash();

        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    public Path getSourceFile() {
        return sourceFile;
    }

    @Override
    public SHA1 sha1() {
        return new SHA1(this);
    }

    @Override
    public File create() throws IOException {
        File tmpFile = File.createTempFile("blob", "file");

        SideEffectWriter objectIO = new SideEffectWriter(tmpFile.toPath());
        objectIO.apply(0, Stream.of(
                new ObjectHeadWriter(this),
                new BlobWrite(this)
        ));

        return tmpFile;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Blob;
    }
}
