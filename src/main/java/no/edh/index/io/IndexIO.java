package no.edh.index.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IndexIO {

    private static final Logger logger = LoggerFactory.getLogger(IndexIO.class);
    private Path index;

    public IndexIO(Path index) {
        this.index = index;
    }

    /**
     * Open file and apply writeoperations to it
     *
     * @param offset initial offset of entry in index file
     * @param ops write operation to apply to index file
     *
     * @return length of bytes written
     */
    public long apply(long offset, Stream<WriteOperation> ops) {
        long length = -1L;
        try (RandomAccessFile fos = new RandomAccessFile(index.toFile(), "rwd")) {
            fos.seek(offset);

            length = ops.mapToLong(writeOperation -> writeOperation.write(fos)).sum();
            fos.close();
        } catch (IOException e) {
            logger.warn("Could not write to index file", e);
        }
        return length;
    }

}
