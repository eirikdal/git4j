package no.edh.index.header.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class HeaderRead implements SideEffect<RandomAccessFile> {
    private static final Logger logger = LoggerFactory.getLogger(HeaderRead.class);

    private static final int OFFSET = 0;

    private Consumer<String> consumer;

    public HeaderRead(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] bytes = new byte[4];
            file.seek(OFFSET);
            file.read(bytes);
            consumer.accept(new String(bytes));
            return file.getFilePointer();
        } catch (IOException e) {
            logger.warn("Error writing index header", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
