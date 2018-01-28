package no.edh.index.header.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class FileCounterRead implements SideEffect<RandomAccessFile> {
    private static final Logger logger = LoggerFactory.getLogger(FileCounterRead.class);

    private static final int OFFSET = 8;
    private Consumer<Long> consumer;

    public FileCounterRead(Consumer<Long> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile index) {
        try {
            index.seek(OFFSET);
            this.consumer.accept((long) index.readInt());
            return index.getFilePointer() - OFFSET;
        } catch (IOException e) {
            logger.warn("Error writing file counter to index header", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
