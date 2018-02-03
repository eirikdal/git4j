package no.edh.index;

import no.edh.hashing.SHA1;
import no.edh.index.entry.IndexEntry;
import no.edh.index.entry.effects.write.FileAttrWrite;
import no.edh.index.file.FileAttr;
import no.edh.index.header.IndexHeader;
import no.edh.index.ops.CacheInfo;
import no.edh.io.SideEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Index {

    private static final Logger logger = LoggerFactory.getLogger(Index.class);

    public static final long OFFSET_FIRST_INDEX_ENTRY = 12L;

    private final Path index;
    private List<IndexEntry> entries = new ArrayList<>();
    private final IndexHeader header;
    private long totalLength = 0;

    public Index(Path index) {
        this.index = index;
        this.header = new IndexHeader(this.index);
    }

    public Path getPath() {
        return index;
    }

    public void init() throws IOException {
        this.index.toFile().createNewFile();
        this.header.init();
        this.totalLength = this.header.getLength();
    }

    /**
     * Add object to .git/objects
     *
     * @param file
     * @throws IOException
     */
    public void addBlobToIndex(CacheInfo file) {
        writeIndexEntry(file);
        updateLength();
    }

    private void writeIndexEntry(CacheInfo path) {
        Long lengthOfEntries = entries.stream().mapToLong(IndexEntry::getIndexEntryLength).sum();
        long offset = header.getLength() + lengthOfEntries;
        IndexEntry entry = new IndexEntry(index, path, offset);
        entry.write(path);
        this.entries.add(entry);
    }

    private void updateLength() {
        long length = 0;
        for (IndexEntry e: entries) {
            length += e.getIndexEntryLength();
        }
        this.totalLength = this.header.getLength() + length;
    }

    /**
     * Update index footer with sha1 of index file and increase file counter in header
     *
     * @throws IOException
     */
    public void updateIndex() {
        this.header.write(this.entries.size());
        FileAttr attr = new FileAttr(SHA1.hashBytes(this.index)); // sha1 of index
        this.totalLength += new SideEffects(this.index).apply(this.totalLength, Stream.of(new FileAttrWrite(attr)));
    }

    public List<IndexEntry> readEntries() {
        List<IndexEntry> entries = new ArrayList<>();
        IndexHeader header = IndexHeader.read(index);

        Long offset = OFFSET_FIRST_INDEX_ENTRY;
        for (int i = 0; i < header.getCount(); i++) {
            IndexEntry entry = IndexEntry.read(index, offset);
            entries.add(entry);
            offset += entry.getIndexEntryLength();
        }

        return entries;
    }

    public void removeEntries() {
        this.entries = new ArrayList<>();
    }
}
