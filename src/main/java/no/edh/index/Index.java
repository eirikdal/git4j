package no.edh.index;

import no.edh.index.entry.IndexEntry;
import no.edh.index.entry.operations.FileAttrWriteOperation;
import no.edh.index.file.FileAttr;
import no.edh.index.header.IndexHeader;
import no.edh.index.io.IndexIO;
import no.edh.objects.GitBlob;
import no.edh.objects.GitObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static no.edh.index.entry.operations.misc.BitWiseOperations.bytesTo12BitInt;

public class Index {
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
    public void addBlobToIndex(GitBlob file) throws IOException {
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
        this.totalLength += new IndexIO(this.index).apply(this.totalLength, Stream.of(new FileAttrWriteOperation(attr)));
    }

    public List<GitObject> findObjects() {
        List<GitObject> objects = new ArrayList<>();
        try (RandomAccessFile fos = new RandomAccessFile(index.toFile(), "r")) {
            byte[] numberOfFileBytes = new byte[4];
            fos.seek(8);
            fos.read(numberOfFileBytes, 0, 4);
            int numberOfFiles = ByteBuffer.wrap(numberOfFileBytes).getInt();

            int offset = 12; // start with the first file
            for (int i = 0; i < numberOfFiles; i++) {
                offset += 40;

                byte[] sha1 = new byte[20];
                fos.seek(offset);
                fos.read(sha1, 0, 20);
                offset += 20;
                String sha1str = new String(Hex.encodeHex(sha1));

                byte[] flags = new byte[4];
                fos.seek(offset); // header + base length
                fos.read(flags, 2, 2);
                offset += 2;
                int fileNameLength = bytesTo12BitInt(flags);

                byte[] b = new byte[fileNameLength];
                fos.read(b, 0, fileNameLength);
                String fileName = new String(b);
                System.out.println(fileName);

                offset += fileNameLength;
                while (fos.readByte() == 0) {
                    offset++;
                }
                // TODO: Figure out type
                GitBlob blob = new GitBlob(Paths.get(System.getProperty("repo.dir"), fileName));
                objects.add(blob);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }
}
