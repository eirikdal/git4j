package no.edh.index.header.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.header.IndexHeader;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class VersionWriteOperation implements WriteOperation {
    private static final Logger logger = LoggerFactory.getLogger(VersionWriteOperation.class);

    private static final int OFFSET = 4;
    public static final int VERSION = 2;

    @Override
    public long write(RandomAccessFile file) {
        try {
            file.seek(OFFSET);
            file.write(new byte[] {0,0,0,2});
            return file.getFilePointer() - OFFSET;
        } catch (IOException e) {
            logger.warn("Error writing version to index header", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
