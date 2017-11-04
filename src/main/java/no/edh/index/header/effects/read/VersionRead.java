package no.edh.index.header.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.misc.BitWiseOperations;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class VersionRead implements SideEffect {
    private static final Logger logger = LoggerFactory.getLogger(VersionRead.class);

    private static final int OFFSET = 4;

    private Consumer<Long> consumer;

    public VersionRead(Consumer<Long> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] bytes = new byte[4];
            file.seek(OFFSET);
            file.read(bytes);
            consumer.accept(BitWiseOperations.bytesToLong(bytes));
            return file.getFilePointer() - OFFSET;
        } catch (IOException e) {
            logger.warn("Error reading version from index header", e);
            throw new SideEffectException("Error reading version from index header", e);
        }
    }
}
