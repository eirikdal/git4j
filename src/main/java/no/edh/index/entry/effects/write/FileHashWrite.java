package no.edh.index.entry.effects.write;

import no.edh.hashing.SHA1;
import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHashWrite implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FileHashWrite.class);

    private final SHA1 attr;

    public FileHashWrite(SHA1 attr) {
        this.attr = attr;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] hashBytes = attr.getHashBytes();
            file.write(hashBytes);
            return hashBytes.length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}

