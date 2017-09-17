package no.edh.index.header;

import no.edh.index.header.operations.FileCounterWriteOperation;
import no.edh.index.header.operations.HeaderWriteOperation;
import no.edh.index.header.operations.VersionWriteOperation;
import no.edh.index.io.IndexIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class IndexHeader {

    private static final Logger logger = LoggerFactory.getLogger(IndexHeader.class);

    private Path index;
    private long length;

    public IndexHeader(Path index) {
        this.index = index;
    }

    /**
     * Initialize index header
     *
     * @throws IOException
     */
    public void init() throws IOException {
        this.length = new IndexIO(this.index).apply(0, Stream.of(
                new HeaderWriteOperation(),
                new VersionWriteOperation(),
                new FileCounterWriteOperation(count -> 0L) // initialize with 0
        ));
    }

    public void write(long count) {
        this.length = new IndexIO(this.index).apply(0, Stream.of(
                new HeaderWriteOperation(),
                new VersionWriteOperation(),
                new FileCounterWriteOperation(current -> count) // initialize with 0
        ));
    }

    public long getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
