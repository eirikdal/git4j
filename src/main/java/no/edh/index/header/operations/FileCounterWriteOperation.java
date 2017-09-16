package no.edh.index.header.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.header.IndexHeader;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.LongFunction;

import static no.edh.index.entry.operations.OperationUtils.longToBytes;

public class FileCounterWriteOperation implements WriteOperation {
    private static final Logger logger = LoggerFactory.getLogger(FileCounterWriteOperation.class);

    private static final int OFFSET = 8;
    private LongFunction<Long> longFunction;

    /**
     * Integer operator to apply to counter
     *
     * @param longFunction
     */
    public FileCounterWriteOperation(LongFunction<Long> longFunction) {
        this.longFunction = longFunction;
    }

    /**
     * Modify counter for number of files in staging area
     *
     * @param index file
     */
    @Override
    public long write(RandomAccessFile index) {
        try {
            long count = 0;
            if (index.length() > 8) {
                index.seek(OFFSET);
                count = (long) index.readInt();
            }
            index.seek(OFFSET);
            index.write(longToBytes(this.longFunction.apply(count)));
            return index.getFilePointer() - OFFSET;
        } catch (IOException e) {
            logger.warn("Error writing file counter to index header", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
