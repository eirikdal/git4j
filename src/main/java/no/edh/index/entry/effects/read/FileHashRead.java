package no.edh.index.entry.effects.read;

import no.edh.hashing.SHA1;
import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.write.FileAttrWrite;
import no.edh.index.file.FileAttr;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class FileHashRead implements SideEffect {
    private static final Logger logger = LoggerFactory.getLogger(FileAttrWrite.class);

    private Consumer<byte[]> consumer;

    public FileHashRead(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] sha1 = new byte[20];
            file.read(sha1, 0, 20);
            this.consumer.accept(sha1);
            return sha1.length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }

}
