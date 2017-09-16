package no.edh.index.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.IntFunction;

public class IndexHeaderWriteOperations {

    private static final Logger logger = LoggerFactory.getLogger(IndexHeader.class);

    interface WriteOperation {
        public void write(RandomAccessFile file);
    }

    static class HeaderWriteOperation implements WriteOperation {
        private static final int OFFSET = 0;

        @Override
        public void write(RandomAccessFile file) {
            try {
                file.seek(OFFSET);
                file.writeBytes("DIRC");
            } catch (IOException e) {
                logger.warn("Error writing index header", e);
            }
        }
    }

    static class VersionWriteOperation implements WriteOperation {
        private static final int OFFSET = 4;
        public static final int VERSION = 2;

        @Override
        public void write(RandomAccessFile file) {
            try {
                file.seek(OFFSET);
                file.writeInt(VERSION);
            } catch (IOException e) {
                logger.warn("Error writing version to index header", e);
            }
        }
    }

    static class CounterWriteOperation implements WriteOperation {
        private static final int OFFSET = 8;
        private IntFunction<Integer> intBinaryOperator;

        /**
         * Integer operator to apply to counter
         *
         * @param intBinaryOperator
         */
        public CounterWriteOperation(IntFunction<Integer> intBinaryOperator) {
            this.intBinaryOperator = intBinaryOperator;
        }

        /**
         * Modify counter for number of files in staging area
         *
         * @param index file
         */
        @Override
        public void write(RandomAccessFile index) {
            try {
                Integer count = 0;
                if (index.length() > 8) {
                    index.seek(OFFSET);
                    count = index.readInt();
                }
                index.seek(OFFSET);
                index.writeInt(this.intBinaryOperator.apply(count));
            } catch (IOException e) {
                logger.warn("Error writing file counter to index header", e);
            }
        }
    }
}
