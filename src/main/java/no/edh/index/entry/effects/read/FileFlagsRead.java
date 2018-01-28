package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.file.FileAttr;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class FileFlagsRead implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FileAttrRead.class);

    private Consumer<FileAttr> consumer;

    public FileFlagsRead(Consumer<FileAttr> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            FileAttr attr = new FileAttr(new byte[2]);
            file.read(attr.bytes(), 0, 2);
            this.consumer.accept(attr);
            return attr.bytes().length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
