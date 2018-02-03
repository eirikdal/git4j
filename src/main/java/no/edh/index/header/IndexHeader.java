package no.edh.index.header;

import no.edh.index.header.effects.read.FileCounterRead;
import no.edh.index.header.effects.read.HeaderRead;
import no.edh.index.header.effects.read.VersionRead;
import no.edh.index.header.effects.write.FileCounterWrite;
import no.edh.index.header.effects.write.HeaderWrite;
import no.edh.index.header.effects.write.VersionWrite;
import no.edh.io.SideEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class IndexHeader {

    private static final Logger logger = LoggerFactory.getLogger(IndexHeader.class);

    private final Path index;
    private long length;

    private Long count = 0L;
    private String header = "DIRC";
    private Long version = 2L;

    public IndexHeader(Path index) {
        this.index = index;
    }

    /**
     * Initialize index header
     *
     * @throws IOException
     */
    public void init() {
        this.length = new SideEffects(this.index).apply(Stream.of(
                new HeaderWrite(this.header),
                new VersionWrite(this.version),
                new FileCounterWrite((current) -> this.count)
        ));
    }

    public void write(long count) {
        this.length = new SideEffects(this.index).apply(Stream.of(
                new HeaderWrite(this.header),
                new VersionWrite(this.version),
                new FileCounterWrite(current -> count) // initialize with 0
        ));
    }

    public static IndexHeader read(Path index) {
        IndexHeader indexEntry = new IndexHeader(index);

        indexEntry.length = new SideEffects(index).apply(Stream.of(
                new HeaderRead(indexEntry::setHeader),
                new VersionRead(indexEntry::setVersion),
                new FileCounterRead(indexEntry::setCount)
        ));

        return indexEntry;
    }

    public long getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getCount() {
        return count;
    }
}
