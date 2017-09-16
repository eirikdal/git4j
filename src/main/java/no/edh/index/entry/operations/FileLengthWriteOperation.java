package no.edh.index.entry.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static no.edh.index.entry.operations.OperationUtils.longToBytes;

public class FileLengthWriteOperation implements WriteOperation {
    private static final Logger logger = LoggerFactory.getLogger(FileLengthWriteOperation.class);

    private Path entry;

    public FileLengthWriteOperation(Path entry) {
        this.entry = entry;
    }

    @Override
    public long write(RandomAccessFile file) {
        long offset, initial;
        try {
            initial = file.getFilePointer();
            file.write(longToBytes(entry.toFile().length()));
            offset = file.getFilePointer();
            return offset - initial;
        } catch (IOException e) {
            logger.error("Error writing file length to index", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
