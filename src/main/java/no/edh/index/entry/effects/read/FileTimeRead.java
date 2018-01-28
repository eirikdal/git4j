package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.write.FileTimeWrite;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static no.edh.index.entry.effects.misc.BitWiseOperations.bytesToLong;

public class FileTimeRead implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FileTimeWrite.class);

    private Consumer<FileTime> time;
    private TimeUnit timeUnit;

    public FileTimeRead(Consumer<FileTime> time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] bytes = new byte[4];
            file.read(bytes, 0, 4);
            this.time.accept(FileTime.from(bytesToLong(bytes), timeUnit));
            file.skipBytes(4);
            return bytes.length+4;
        } catch (IOException e) {
            logger.error("Error writing file time", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
