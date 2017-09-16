package no.edh.index.header.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HeaderWriteOperation implements WriteOperation {
    private static final Logger logger = LoggerFactory.getLogger(HeaderWriteOperation.class);

    private static final int OFFSET = 0;

    @Override
    public long write(RandomAccessFile file) {
        try {
            file.seek(OFFSET);
            file.writeBytes("DIRC");
            return file.getFilePointer();
        } catch (IOException e) {
            logger.warn("Error writing index header", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
