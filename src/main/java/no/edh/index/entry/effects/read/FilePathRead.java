package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.write.FilePathWrite;
import no.edh.index.io.SideEffect;
import no.edh.objects.GitBlob;
import no.edh.objects.GitObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.function.Consumer;

import static java.lang.System.in;

public class FilePathRead implements SideEffect {

    private static final Logger logger = LoggerFactory.getLogger(FilePathWrite.class);

    private Consumer<GitObject> consumer;

    public FilePathRead(Consumer<GitObject> consumer) {
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
            this.consumer.accept(new GitBlob(Paths.get(new String(baos.toByteArray()))));
            return baos.size();
        } catch (IOException e) {
            logger.error("Error writing file path entry", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
