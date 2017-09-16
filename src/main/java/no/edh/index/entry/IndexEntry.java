package no.edh.index.entry;

import no.edh.index.entry.operations.FileAttrWriteOperation;
import no.edh.index.entry.operations.FileLengthWriteOperation;
import no.edh.index.entry.operations.FilePathWriteOperation;
import no.edh.index.entry.operations.FileTimeWriteOperation;
import no.edh.index.file.FileAttr;
import no.edh.index.io.IndexIO;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import static no.edh.index.entry.operations.FileAttrWriteOperation.*;

public class IndexEntry {

    private Path index;
    private Path entry;
    private long offset;
    private long length;

    public IndexEntry(Path index, Path entry, long offset) {
        this.index = index;
        this.entry = entry;
        this.offset = offset;
    }

    public void write() throws IOException {
        BasicFileAttributes attr = Files.readAttributes(entry, BasicFileAttributes.class);

        this.length = new IndexIO(this.index).apply(offset, Stream.of(
                new FileTimeWriteOperation(attr.creationTime(), TimeType.SECONDS),
                new FileTimeWriteOperation(attr.creationTime(), TimeType.NANOSECONDS),
                new FileTimeWriteOperation(attr.lastModifiedTime(), TimeType.SECONDS),
                new FileTimeWriteOperation(attr.lastModifiedTime(), TimeType.NANOSECONDS),
                new FileAttrWriteOperation(new FileAttr(new byte[4])), // device
                new FileAttrWriteOperation(new FileAttr(new byte[4])), // inode
                new FileAttrWriteOperation(new FileAttr(new byte[] { 0,0,(byte) 0201, (byte)0244 })), // mode
                new FileAttrWriteOperation(new FileAttr(new byte[4])), //userId
                new FileAttrWriteOperation(new FileAttr(new byte[4])), //groupId
                new FileLengthWriteOperation(entry), //fileLength
                new FileAttrWriteOperation(new FileAttr(DigestUtils.sha1(Files.readAllBytes(this.entry)))), //sha1
                new FileAttrWriteOperation(new FileAttr(new byte[] {0,8})), //flags
                new FilePathWriteOperation(entry), //path
                new FileAttrWriteOperation(new FileAttr(new byte[] {0})) //0-padding
        ));
    }

    public long getLength() {
        return length;
    }
}
