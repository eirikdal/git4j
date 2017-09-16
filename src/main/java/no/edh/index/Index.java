package no.edh.index;

import no.edh.index.header.IndexHeader;
import no.edh.objects.GitObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Index {
    private Path index;

    public Index(Path index) {
        this.index = index;
    }

    public Path getPath() {
        return index;
    }

    public void init() throws IOException {
        boolean newFile = this.index.toFile().createNewFile();
        new IndexHeader(this.index).createHeader();
    }

    public void addObjectToIndex(GitObject file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.getPath(), BasicFileAttributes.class);

        FileOutputStream fos = new FileOutputStream(index.toFile(), true);
        DataOutputStream outputStream = new DataOutputStream(fos);

        outputStream.write(createdTimeSeconds(attr)); // 4
        outputStream.write(createdTimeNanoSeconds(attr)); //8
        outputStream.write(modifiedTimeSeconds(attr));
        outputStream.write(modifiedTimeNanoSeconds(attr)); //16
        outputStream.write(device());
        outputStream.write(inode());
        outputStream.write(mode());
        outputStream.write(userId()); // 32
        outputStream.write(groupId());
//        outputStream.write(fileContentLength(file));
        outputStream.write(new byte[] {0,0,0,4}); // 40 file length?
        outputStream.write(DigestUtils.sha1(Files.readAllBytes(file.getPath()))); // 44
        outputStream.write(flags()); // 48
//        outputStream.write(flags2());
        outputStream.write(path(file.getPath())); // variable length
//        outputStream.write(new byte[] {0}); // zero-pad length up to mod 8
        outputStream.write(new byte[] {0});
        outputStream.write(DigestUtils.sha1(Files.readAllBytes(index)));
        outputStream.close();
        fos.close();
    }

    public byte[] longToBytes(long unixTime) {
        return new byte[]{
                (byte) (unixTime >> 24),
                (byte) (unixTime >> 16),
                (byte) (unixTime >> 8),
                (byte) unixTime

        };
    }

    private byte[] path(Path file) {
        URI testrepo = Paths.get(System.getProperty("user.dir"), "testrepo").toUri();
        URI fileUri = file.toFile().toURI();

        URI relativize = testrepo.relativize(fileUri);
        return "testfile".getBytes();
    }

    private byte[] flags2() {
        return new byte[] {0,2};
    }

    private byte[] flags() {
        return new byte[] { 0,8 };
    }

    private byte[] fileContentLength(Path file) {
        return longToBytes(file.toFile().length());
    }

    private byte[] groupId() {
        return new byte[4];
    }

    private byte[] userId() {
        return new byte[4];
    }

    private byte[] mode() {
        return new byte[] { 0,0,(byte) 0201, (byte)0244 };
    }

    private byte[] inode() {
        return new byte[4];
    }

    private byte[] device() {
        return new byte[] { 0,0,0,0 };
    }

    private byte[] modifiedTimeNanoSeconds(BasicFileAttributes attr) {
        return longToBytes(attr.creationTime().toMillis() * 1000L);
    }

    private byte[] modifiedTimeSeconds(BasicFileAttributes attr) {
        return longToBytes(attr.creationTime().toInstant().getEpochSecond());
    }

    private byte[] createdTimeNanoSeconds(BasicFileAttributes attr) {
        return longToBytes(attr.creationTime().toMillis() * 1000L);
    }

    private byte[] createdTimeSeconds(BasicFileAttributes attr) {
        return longToBytes(attr.creationTime().toInstant().getEpochSecond());
    }
}
