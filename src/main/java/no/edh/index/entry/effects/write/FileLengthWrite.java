package no.edh.index.entry.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static no.edh.index.entry.effects.misc.BitWiseOperations.longToBytes;

public class FileLengthWrite implements SideEffect {
    private static final Logger logger = LoggerFactory.getLogger(FileLengthWrite.class);

    private Path entry;

    public FileLengthWrite(Path entry) {
        this.entry = entry;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] bytes = longToBytes(entry.toFile().length());
            file.write(bytes);
            return bytes.length;
        } catch (IOException e) {
            logger.error("Error writing file length to index", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
