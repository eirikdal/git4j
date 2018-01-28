package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.write.FileAttrWrite;
import no.edh.index.file.FileAttr;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class FileAttrRead implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FileAttrWrite.class);

    private Consumer<FileAttr> consumer;

    public FileAttrRead(Consumer<FileAttr> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            FileAttr attr = new FileAttr(new byte[4]);
            file.read(attr.bytes(), 0, 4);
            this.consumer.accept(attr);
            return attr.bytes().length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}