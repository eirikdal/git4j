package no.edh.index.entry.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

import static no.edh.index.entry.effects.misc.BitWiseOperations.longToBytes;

public class FileTimeWrite implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(FileTimeWrite.class);

    private FileTime time;
    private TimeUnit timeType;

    public FileTimeWrite(FileTime time, TimeUnit timeType) {
        this.time = time;
        this.timeType = timeType;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            byte[] bytes = new byte[0];
            switch (timeType) {
                case SECONDS:
                    bytes = longToBytes(time.toMillis() / 1000L);
                    file.write(bytes);
                    break;
                case MILLISECONDS:
                    bytes = longToBytes(time.toInstant().getEpochSecond());
                    file.write(bytes);
                    break;
                case NANOSECONDS:
                    bytes = longToBytes(time.toMillis() * 1000L);
                    file.write(bytes);
                    break;
            }
            return bytes.length;
        } catch (IOException e) {
            logger.error("Error writing file time", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
