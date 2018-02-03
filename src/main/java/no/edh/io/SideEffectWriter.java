package no.edh.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SideEffectWriter {

    private static final Logger logger = LoggerFactory.getLogger(SideEffectWriter.class);
    private Path path;

    public SideEffectWriter(Path path) {
        this.path = path;
    }

    /**
     * Open file and apply writeoperations to it
     *
     * @param offset initial offset of entry in index file
     * @param ops write operation to apply to index file
     *
     * @return length of bytes written
     */
    public long apply(long offset, Stream<SideEffect<RandomAccessFile>> ops) {
        long length = -1L;
        try (RandomAccessFile fos = new RandomAccessFile(path.toFile(), "rwd")) {
            fos.seek(offset);
            length = ops.mapToLong(sideEffect -> sideEffect.apply(fos)).sum();
            fos.close();
        } catch (IOException e) {
            logger.warn("Could not perform sid", e);
        }
        return length;
    }

}
