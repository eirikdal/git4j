package no.edh.objects.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.objects.GitObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BlobHeadWrite implements SideEffect<RandomAccessFile> {
    private static final Logger logger = LoggerFactory.getLogger(BlobHeadWrite.class);

    private final GitObject entry;

    public BlobHeadWrite(GitObject entry) {
        this.entry = entry;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            file.write(entry.objectType().getType().getBytes());
            file.write(" ".getBytes());
            file.write(String.format("%d", entry.realPath().toFile().length()).getBytes());
            file.write(new byte[]{0});

            return file.length();
        } catch (IOException e) {
            logger.error("Error writing object head", e);
            throw new SideEffectException("Error writing object head", e);
        }
    }
}
