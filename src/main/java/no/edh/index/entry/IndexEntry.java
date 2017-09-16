package no.edh.index.entry;

import no.edh.index.entry.operations.*;
import no.edh.index.file.FileAttr;
import no.edh.index.io.IndexIO;
import no.edh.objects.GitBlob;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class IndexEntry {

    private Path index;
    private GitBlob entry;
    private long offset;
    private long length;

    public IndexEntry(Path index, GitBlob entry, long offset) {
        this.index = index;
        this.entry = entry;
        this.offset = offset;
    }

    public void write() throws IOException {
        BasicFileAttributes attr = Files.readAttributes(entry.getWorkingFilePath(), BasicFileAttributes.class);

        FileInputStream data = new FileInputStream(this.entry.getObjectsStream(this.entry));
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
                new FileLengthWriteOperation(entry.getWorkingFilePath()), //fileLength
                new FileAttrWriteOperation(new FileAttr(DigestUtils.sha1(data))), //sha1
                new FileAttrWriteOperation(new FileAttr(new byte[] {0,8})), //flags
                new FilePathWriteOperation(entry.getWorkingFilePath()), //path
                new FileAttrWriteOperation(new FileAttr(new byte[] {0,0})) //0-padding
        ));
        data.close();
    }

    public long getLength() {
        return length;
    }
}
