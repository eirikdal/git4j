package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.write.FilePathWrite;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class FilePathRead implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FilePathWrite.class);

    private final Consumer<Path> consumer;

    public FilePathRead(Consumer<Path> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte b = file.readByte();
            while (b != 0) {
                baos.write(b);
                b = file.readByte();
            }
            file.seek(file.getFilePointer()-1);
            this.consumer.accept(Paths.get(new String(baos.toByteArray())));
            return baos.size();
        } catch (IOException e) {
            logger.error("Error writing file path entry", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
