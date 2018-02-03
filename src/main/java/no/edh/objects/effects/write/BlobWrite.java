package no.edh.objects.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.objects.Blob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public class BlobWrite implements SideEffect<RandomAccessFile> {

    private final Blob blob;

    public BlobWrite(Blob blob) {
        this.blob = blob;
    }

    @Override
    public long apply(RandomAccessFile blobOutputFile) {
        File blobFile = blob.realPath().toFile();
        try {
            FileOutputStream out = new FileOutputStream(blobOutputFile.getFD());
            Files.copy(blob.realPath(), out);
            out.flush();
            return blobFile.length();
        } catch (IOException e) {
            throw new SideEffectException("Failed to write blob", e);
        }
    }
}
