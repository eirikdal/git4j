package no.edh.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SideEffects {

    private static final Logger logger = LoggerFactory.getLogger(SideEffects.class);
    public static final int OFFSET_ZERO = 0;
    private final Path path;

    public SideEffects(Path path) {
        this.path = path;
    }


    /**
     * Open file and apply writeoperations to it
     *
     * @param sideEffects write operation to apply to index file
     *
     * @return length of bytes written
     */
    public long apply(Stream<SideEffect<RandomAccessFile>> sideEffects) {
        return apply(OFFSET_ZERO, sideEffects);
    }

    /**
     * Open file and apply writeoperations to it
     *
     * @param offset initial offset of entry in index file
     * @param sideEffects write operation to apply to index file
     *
     * @return length of bytes written
     */
    public long apply(long offset, Stream<SideEffect<RandomAccessFile>> sideEffects) {
        long length = -1L;
        try (RandomAccessFile fos = new RandomAccessFile(path.toFile(), "rwd")) {
            fos.seek(offset);
            length = sideEffects.mapToLong(sideEffect -> sideEffect.apply(fos)).sum();
        } catch (IOException e) {
            logger.warn("Could not perform side effects", e);
        }
        return length;
    }
}
