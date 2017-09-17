package no.edh.index.entry.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.entry.operations.misc.TimeType;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;

import static no.edh.index.entry.operations.misc.BitWiseOperations.longToBytes;

public class FileTimeWriteOperation implements WriteOperation {

    private static final Logger logger = LoggerFactory.getLogger(FileTimeWriteOperation.class);

    private FileTime time;
    private TimeType timeType;

    public FileTimeWriteOperation(FileTime time, TimeType timeType) {
        this.time = time;
        this.timeType = timeType;
    }

    @Override
    public long write(RandomAccessFile file) {
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
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
