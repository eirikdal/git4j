package no.edh.index;

import no.edh.index.entry.IndexEntry;
import no.edh.index.entry.operations.FileAttrWriteOperation;
import no.edh.index.file.FileAttr;
import no.edh.index.header.IndexHeader;
import no.edh.index.io.IndexIO;
import no.edh.objects.GitBlob;
import no.edh.objects.GitObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Index {
    private Path index;
    private List<IndexEntry> entries = new ArrayList<IndexEntry>();
    private IndexHeader header;
    private long totalLength = 0;

    public Index(Path index) {
        this.index = index;
    }

    public Path getPath() {
        return index;
    }

    public void init() throws IOException {
        this.index.toFile().createNewFile();
        this.header = new IndexHeader(this.index);
        this.header.init();
        this.totalLength += this.header.getLength();
    }

    /**
     * Add object to .git/objects
     *
     * @param file
     * @throws IOException
     */
    public void addObjectToIndex(GitBlob file) throws IOException {
        IndexEntry entry = writeIndexEntry(file);
        updateLength(entry);
        updateIndex();
    }

    private IndexEntry writeIndexEntry(GitBlob blob) throws IOException {
        IndexEntry entry = new IndexEntry(index, blob, header.getLength());
        entry.write();
        entries.add(entry);
        return entry;
    }

    private void updateLength(IndexEntry entry) {
        LongStream stream = entries.stream().mapToLong(value -> entry.getLength());
        this.totalLength += stream.sum();
    }

    /**
     * Update index footer with sha1 of index file and increase file counter in header
     *
     * @throws IOException
     */
    private void updateIndex() throws IOException {
        this.header.write(this.entries.size());
        FileAttr attr = new FileAttr(DigestUtils.sha1(Files.readAllBytes(this.index))); // sha1 of index

        this.totalLength += new IndexIO(this.index).apply(this.totalLength, Stream.of(new FileAttrWriteOperation(attr)));
    }
}
