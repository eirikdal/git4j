package no.edh.index.header.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.LongFunction;

import static no.edh.index.entry.effects.misc.BitWiseOperations.longToBytes;

public class FileCounterWrite implements SideEffect<RandomAccessFile> {
    private static final Logger logger = LoggerFactory.getLogger(FileCounterWrite.class);

    private static final int OFFSET = 8;
    private final LongFunction<Long> longFunction;

    /**
     * Integer operator to apply to counter
     *
     * @param longFunction
     */
    public FileCounterWrite(LongFunction<Long> longFunction) {
        this.longFunction = longFunction;
    }

    /**
     * Modify counter for number of files in staging area
     *
     * @param index file
     */
    @Override
    public long apply(RandomAccessFile index) {
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
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
