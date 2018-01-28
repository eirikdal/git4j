package no.edh.objects.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.objects.Blob;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;

public class BlobWrite implements SideEffect<RandomAccessFile> {

    private final Blob blob;

    public BlobWrite(Blob blob) {
        this.blob = blob;
    }

    @Override
    public long apply(RandomAccessFile blobOutputFile) {
        File blobFile = blob.getSourceFile().toFile();
        try {
            FileOutputStream out = new FileOutputStream(blobOutputFile.getFD());
            Files.copy(blob.getSourceFile(), out);
            out.flush();
            return blobFile.length();
        } catch (IOException e) {
            throw new SideEffectException("Failed to write blob", e);
        }
    }
}
