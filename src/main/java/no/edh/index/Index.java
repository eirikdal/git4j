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
        writeIndexEntry(file);
        updateLength();
    }

    private void writeIndexEntry(GitBlob blob) throws IOException {
        Long lengthOfEntries = entries.stream().mapToLong(value -> value.getLength()).sum();
        long offset = header.getLength() + lengthOfEntries;
        IndexEntry entry = new IndexEntry(index, blob, offset);
        entry.write();
        entries.add(entry);
    }

    private void updateLength() {
        long length = 0;
        for (IndexEntry e: entries) {
            length += e.getLength();
        }
        this.totalLength = this.header.getLength() + length;
    }

    /**
     * Update index footer with sha1 of index file and increase file counter in header
     *
     * @throws IOException
     */
    public void updateIndex() throws IOException {
        this.header.write(this.entries.size());
        FileAttr attr = new FileAttr(DigestUtils.sha1(Files.readAllBytes(this.index))); // sha1 of index
        // TODO: total length is somewhat off here.. figure out why
        this.totalLength += new IndexIO(this.index).apply(this.totalLength, Stream.of(new FileAttrWriteOperation(attr)));
    }
}
