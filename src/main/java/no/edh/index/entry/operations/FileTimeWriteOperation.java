package no.edh.index.entry.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;

import static no.edh.index.entry.operations.OperationUtils.longToBytes;

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
        long offset, initial;
        try {
            initial = file.getFilePointer();

            switch (timeType) {
                case SECONDS:
                    file.write(longToBytes(time.toMillis() / 1000L));
                    break;
                case MILLISECONDS:
                    file.write(longToBytes(time.toInstant().getEpochSecond()));
                    break;
                case NANOSECONDS:
                    file.write(longToBytes(time.toMillis() * 1000L));
                    break;
            }
            offset = file.getFilePointer();
            return offset - initial;
        } catch (IOException e) {
            logger.error("Error writing file time", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
