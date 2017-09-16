package no.edh.index.header;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.LongFunction;

import static no.edh.index.entry.operations.OperationUtils.longToBytes;

public class IndexHeaderWriteOperations {

    private static final Logger logger = LoggerFactory.getLogger(IndexHeader.class);

    static class HeaderWriteOperation implements WriteOperation {
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

    static class VersionWriteOperation implements WriteOperation {
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

    static class FileCounterWriteOperation implements WriteOperation {
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
}
