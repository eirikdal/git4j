package no.edh.index.entry;

import no.edh.index.entry.operations.*;
import no.edh.index.entry.operations.misc.TimeType;
import no.edh.index.file.FileAttr;
import no.edh.index.io.IndexIO;
import no.edh.objects.GitBlob;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

import static no.edh.index.Index.OFFSET_SHA1;
import static no.edh.index.Index.SIZE_OF_FLAGS;
import static no.edh.index.Index.SIZE_OF_SHA1;
import static no.edh.index.entry.operations.misc.BitWiseOperations.bytesTo12BitInt;

public class IndexEntry {

    private Logger logger = LoggerFactory.getLogger(IndexEntry.class);

    private Path index;
    private GitBlob entry;
    private long offset;
    private long length;
    private FileTime creationTime;
    private FileTime modifiedTime;
    private byte[] device;
    private byte[] inode;
    private byte[] mode;
    private byte[] userId;
    private byte[] groupId;
    private byte[] sha1;
    private Path path;

    public IndexEntry(Path index, GitBlob entry, long offset) {
        this.index = index;
        this.entry = entry;
        this.offset = offset;
        this.path = entry.getSourceFile();

        read(entry);
    }

    private IndexEntry() {

    }

    private void read(GitBlob entry) {
        try(FileInputStream fis = new FileInputStream(path.toFile())) {
            BasicFileAttributes attr = Files.readAttributes(entry.getSourceFile(), BasicFileAttributes.class);
            this.creationTime = attr.creationTime();
            this.modifiedTime = attr.lastModifiedTime();
            this.device = new byte[4];
            this.inode = new byte[4];
            this.mode = new byte[] { 0, 0, (byte) 0201, (byte) 0244 };
            this.userId = new byte[4];
            this.groupId = new byte[4];
            this.sha1 = DigestUtils.sha1(fis);
            this.entry.create();
        } catch (IOException e) {
            logger.error("Error reading index entry", e);
        }
    }

    public void write() throws IOException {
        this.length = new IndexIO(this.index).apply(offset, Stream.of(
                new FileTimeWriteOperation(this.creationTime, TimeType.SECONDS),// 4
                new FileTimeWriteOperation(this.creationTime, TimeType.NANOSECONDS), //8
                new FileTimeWriteOperation(this.modifiedTime, TimeType.SECONDS),//12
                new FileTimeWriteOperation(this.modifiedTime, TimeType.NANOSECONDS), //16
                new FileAttrWriteOperation(new FileAttr(this.device)), // device 20
                new FileAttrWriteOperation(new FileAttr(this.inode)), // inode 24
                new FileAttrWriteOperation(new FileAttr(this.mode)), // mode 28
                new FileAttrWriteOperation(new FileAttr(this.userId)), //userId 32
                new FileAttrWriteOperation(new FileAttr(this.groupId)), //groupId 36
                new FileLengthWriteOperation(this.path), //fileLength 40
                new FileAttrWriteOperation(new FileAttr(this.sha1)), //sha1 60
                new FileFlagsWriteOperation(this.path), //flags 62
//                new FileAttrWriteOperation(new FileAttr(new byte[] {0,0})), //flags 62
                new FilePathWriteOperation(this.path), //path 70
                new ZeroPaddingWriteOperation(this.path) //zero-padding 71
        ));
    }

    public long getLength() {
        return length;
    }

    public static IndexEntry read(RandomAccessFile fos) throws IOException {
        IndexEntry indexEntry = new IndexEntry();

        fos.seek(fos.getFilePointer() + OFFSET_SHA1); // TODO: read index entry header, for now go directly to sha1

        // TODO: Don't really need to extract sha1 at this point, maybe in the future tho.
        byte[] sha1 = getSha1(fos);

        // extract length of filename
        int fileNameLength = getFileNameLength(fos);

        // extract filename, accounting for extra zero-padding
        String fileName = getFileName(fos, fileNameLength);

        long filePointer = fos.getFilePointer();
        while (fos.readByte() == 0) {
            fos.seek(filePointer);
            filePointer++;
        }
        fos.seek(filePointer-1);
        indexEntry.path = Paths.get(fileName);
        indexEntry.sha1 = sha1;
        return indexEntry;
    }


    private static String getFileName(RandomAccessFile fos, int fileNameLength) throws IOException {
        byte[] b = new byte[fileNameLength];
        fos.read(b, 0, fileNameLength);
        return new String(b);
    }

    private static int getFileNameLength(RandomAccessFile fos) throws IOException {
        byte[] flags = new byte[SIZE_OF_FLAGS];
        fos.read(flags, 2, SIZE_OF_FLAGS-2);
        return bytesTo12BitInt(flags);
    }

    private static byte[] getSha1(RandomAccessFile fos) throws IOException {
        byte[] sha1 = new byte[SIZE_OF_SHA1];
        fos.read(sha1, 0, SIZE_OF_SHA1);
        return sha1;
    }

    public Path getPath() {
        return path;
    }
}
