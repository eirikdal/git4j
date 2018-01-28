package no.edh.index.entry.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHashWrite implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FileHashWrite.class);

    private byte[] attr;

    public FileHashWrite(byte[] attr) {
        this.attr = attr;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            file.write(attr);
            return attr.length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}

