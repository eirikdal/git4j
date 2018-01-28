package no.edh.index.entry;

import no.edh.index.entry.effects.exceptions.IndexEntryReadException;
import no.edh.index.entry.effects.read.*;
import no.edh.index.entry.effects.write.*;
import no.edh.index.file.FileAttr;
import no.edh.io.SideEffectWriter;
import no.edh.objects.GitObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class IndexEntry {

    private Logger logger = LoggerFactory.getLogger(IndexEntry.class);

    private Path index;
    private Long offset;
    private Long indexEntryLength;
    private FileTime creationTime;
    private FileTime modifiedTime;
    private FileAttr device;
    private FileAttr inode;
    private FileAttr mode;
    private FileAttr userId;
    private FileAttr groupId;
    private byte[] sha1;
    private FileAttr flags;
    private Long fileLength;
    private GitObject object;

    public IndexEntry(Path index, GitObject entry, long offset) {
        this.index = index;
        this.object = entry;
        this.offset = offset;

        read(entry);
    }

    private IndexEntry() {

    }

    private void read(GitObject entry) {
        try {
            BasicFileAttributes attr = Files.readAttributes(entry.getSourceFile(), BasicFileAttributes.class);
            this.creationTime = attr.creationTime();
            this.modifiedTime = attr.lastModifiedTime();
            this.device = new FileAttr(new byte[4]);
            this.inode = new FileAttr(new byte[4]);
            this.mode = new FileAttr(new byte[] { 0, 0, (byte) 0201, (byte) 0244 });
            this.userId = new FileAttr(new byte[4]);
            this.groupId = new FileAttr(new byte[4]);
            this.flags = new FileAttr(new byte[2]);
            this.sha1 = entry.sha1().hashBytes();
        } catch (IOException e) {
            logger.error("Error reading index entry", e);
            throw new IndexEntryReadException("Error reading index entry", e);
        }
    }

    public void write() {
        this.indexEntryLength = new SideEffectWriter(this.index).apply(offset, Stream.of(
                new FileTimeWrite(this.creationTime, TimeUnit.SECONDS),// 4
                new FileTimeWrite(this.creationTime, TimeUnit.NANOSECONDS), //8
                new FileTimeWrite(this.modifiedTime, TimeUnit.SECONDS),//12
                new FileTimeWrite(this.modifiedTime, TimeUnit.NANOSECONDS), //16
                new FileAttrWrite(this.device), // device 20
                new FileAttrWrite(this.inode), // inode 24
                new FileAttrWrite(this.mode), // mode 28
                new FileAttrWrite(this.userId), //userId 32
                new FileAttrWrite(this.groupId), //groupId 36
                new FileLengthWrite(this.object.getSourceFile()), //fileLength 40
                new FileHashWrite(this.sha1), //sha1 60
                new FileFlagsWrite(this.object.getSourceFile()), //flags 62
                new FilePathWrite(this.object.getSourceFile()), //object 70
                new ZeroPaddingWrite(this.object.getSourceFile()) //zero-padding 71
        ));
    }

    public static IndexEntry read(Path index, Long offset) {
        IndexEntry indexEntry = new IndexEntry();

        indexEntry.indexEntryLength = new SideEffectWriter(index).apply(offset, Stream.of(
                new FileTimeRead(indexEntry::setCreationTime, TimeUnit.SECONDS),
                new FileTimeRead(indexEntry::setModifiedTime, TimeUnit.SECONDS),
                new FileAttrRead(indexEntry::setDevice),
                new FileAttrRead(indexEntry::setInode),
                new FileAttrRead(indexEntry::setMode),
                new FileAttrRead(indexEntry::setUserId),
                new FileAttrRead(indexEntry::setGroupId),
                new FileLengthRead(indexEntry::setFileLength),
                new FileHashRead(indexEntry::setSha1),
                new FileFlagsRead(indexEntry::setFlags),
                new FilePathRead(indexEntry::setObject),
                new ZeroPaddingRead()
        ));

        return indexEntry;
    }

    public GitObject getObject() {
        return object;
    }

    public long getIndexEntryLength() {
        return indexEntryLength;
    }

    public void setMode(FileAttr mode) {
        this.mode = mode;
    }

    public void setInode(FileAttr inode) {
        this.inode = inode;
    }

    public void setCreationTime(FileTime creationTime) {
        this.creationTime = creationTime;
    }

    public void setModifiedTime(FileTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public void setDevice(FileAttr device) {
        this.device = device;
    }

    public void setUserId(FileAttr userId) {
        this.userId = userId;
    }

    public void setGroupId(FileAttr groupId) {
        this.groupId = groupId;
    }

    public void setSha1(byte[] sha1) {
        this.sha1 = sha1;
    }

    public void setFlags(FileAttr flags) {
        this.flags = flags;
    }

    public void setIndexEntryLength(Long indexEntryLength) {
        this.indexEntryLength = indexEntryLength;
    }

    public void setObject(GitObject object) {
        this.object = object;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }
}
