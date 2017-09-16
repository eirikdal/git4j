package no.edh.index.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.stream.Stream;

import static no.edh.index.header.IndexHeaderWriteOperations.*;

public class IndexHeader {

    private static final Logger logger = LoggerFactory.getLogger(IndexHeader.class);

    private static final int OFFSET = 0;
    private static final int LENGTH = 12;
    private Path index;

    public IndexHeader(Path index) {
        this.index = index;
    }

    /**
     * Open file and apply writeoperations to it
     *
     * @param ops
     */
    private void openStream(Stream<WriteOperation> ops) {
        try (RandomAccessFile fos = new RandomAccessFile(index.toFile(), "rwd")) {
            ops.forEach(writeOperation -> writeOperation.write(fos));
        } catch (IOException e) {
            logger.warn("Could not write to index file", e);
        }
    }

    /**
     * Initialize index header
     *
     * @throws IOException
     */
    public void createHeader() throws IOException {
        openStream(Stream.of(
                new HeaderWriteOperation(),
                new VersionWriteOperation(),
                new CounterWriteOperation(count -> 0) // initialize with 0
        ));
    }
}
