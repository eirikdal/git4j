package no.edh.index.entry;

import no.edh.index.entry.operations.*;
import no.edh.index.entry.operations.misc.TimeType;
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
                new FileTimeWriteOperation(attr.creationTime(), TimeType.SECONDS),// 4
                new FileTimeWriteOperation(attr.creationTime(), TimeType.NANOSECONDS), //8
                new FileTimeWriteOperation(attr.lastModifiedTime(), TimeType.SECONDS),//12
                new FileTimeWriteOperation(attr.lastModifiedTime(), TimeType.NANOSECONDS), //16
                new FileAttrWriteOperation(new FileAttr(new byte[4])), // device 20
                new FileAttrWriteOperation(new FileAttr(new byte[4])), // inode 24
                new FileAttrWriteOperation(new FileAttr(new byte[] { 0,0,(byte) 0201, (byte)0244 })), // mode 28
                new FileAttrWriteOperation(new FileAttr(new byte[4])), //userId 32
                new FileAttrWriteOperation(new FileAttr(new byte[4])), //groupId 36
                new FileLengthWriteOperation(entry.getWorkingFilePath()), //fileLength 40
                new FileAttrWriteOperation(new FileAttr(DigestUtils.sha1(data))), //sha1 60
                new FileFlagsWriteOperation(entry.getWorkingFilePath()), //flags 62
//                new FileAttrWriteOperation(new FileAttr(new byte[] {0,0})), //flags 62
                new FilePathWriteOperation(entry.getWorkingFilePath()), //path 70
                new ZeroPaddingWriteOperation(entry.getWorkingFilePath()) //zero-padding 71
        ));
        data.close();
    }

    public long getLength() {
        return length;
    }
}
