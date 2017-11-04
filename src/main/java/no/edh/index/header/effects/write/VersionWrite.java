package no.edh.index.header.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.misc.BitWiseOperations;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class VersionWrite implements SideEffect {
    private static final Logger logger = LoggerFactory.getLogger(VersionWrite.class);

    private static final int OFFSET = 4;

    private final Long version;

    public VersionWrite(Long version) {
        this.version = version;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            file.seek(OFFSET);
            file.write(BitWiseOperations.longToBytes(version));
            return file.getFilePointer() - OFFSET;
        } catch (IOException e) {
            logger.warn("Error writing version to index header", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
