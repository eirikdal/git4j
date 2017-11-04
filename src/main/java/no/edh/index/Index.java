package no.edh.index;

import no.edh.index.entry.IndexEntry;
import no.edh.index.entry.operations.FileAttrWriteOperation;
import no.edh.index.file.FileAttr;
import no.edh.index.header.IndexHeader;
import no.edh.index.io.IndexIO;
import no.edh.objects.GitBlob;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Index {

    private static final Logger logger = LoggerFactory.getLogger(Index.class);

    public static final int OFFSET_FIRST_INDEX_ENTRY = 12;
    public static final int OFFSET_SHA1 = 40;
    public static final int SIZE_OF_SHA1 = 20;
    public static final int SIZE_OF_FLAGS = 4;
    public static final int HEADER_SIZE_NUMBER_OF_FILES = 4;
    public static final int START_OF_HEADER_NUMBER_OF_FILES = 8;

    private Path index;
    private List<IndexEntry> entries = new ArrayList<IndexEntry>();
    private IndexHeader header;
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
        this.totalLength += this.header.getLength();
    }

    /**
     * Add object to .git/objects
     *
     * @param file
     * @throws IOException
     */
    public void addBlobToIndex(GitBlob file) {
        try {
            writeIndexEntry(file);
            updateLength();
        } catch (IOException e) {
            logger.error("Failed to write index file", e);
        }
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
        this.totalLength += new IndexIO(this.index).apply(this.totalLength, Stream.of(new FileAttrWriteOperation(attr)));
    }

    public List<IndexEntry> readEntries() {
        List<IndexEntry> entries = new ArrayList<>();
        try (RandomAccessFile fos = new RandomAccessFile(index.toFile(), "r")) {
            int numberOfFiles = readNumberOfFiles(fos);

            fos.seek(OFFSET_FIRST_INDEX_ENTRY); // start with the first file
            for (int i = 0; i < numberOfFiles; i++) {
                entries.add(IndexEntry.read(fos));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private int readNumberOfFiles(RandomAccessFile fos) throws IOException {
        byte[] numberOfFileBytes = new byte[HEADER_SIZE_NUMBER_OF_FILES];
        fos.seek(START_OF_HEADER_NUMBER_OF_FILES);
        fos.read(numberOfFileBytes, 0, HEADER_SIZE_NUMBER_OF_FILES);
        return ByteBuffer.wrap(numberOfFileBytes).getInt();
    }
}
