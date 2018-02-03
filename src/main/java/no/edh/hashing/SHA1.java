package no.edh.hashing;

import no.edh.hashing.exceptions.HashEncodingException;
import no.edh.objects.GitObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class SHA1 {

    private byte[] hashBytes;
    private String hashHex;

    public SHA1(GitObject source) {
        this.hashBytes = hashBytes(source);
        this.hashHex = hash(source);
    }

    public SHA1(byte[] hashBytes) {
        this.hashBytes = hashBytes;
    }

    public SHA1(String hashHex) {
        this.hashHex = hashHex;
    }

    public byte[] getHashBytes() {
        return hashBytes;
    }

    public String getHashHex() {
        return hashHex;
    }

    public static byte[] hashBytes(GitObject source) {
        try (FileInputStream inputStream = new FileInputStream(source.write())) {
            return DigestUtils.sha1(inputStream);
        } catch (IOException e) {
            throw new HashEncodingException(e);
        }
    }

    public static String hash(Path source) {
        try (FileInputStream inputStream = new FileInputStream(source.toFile())) {
            return DigestUtils.sha1Hex(inputStream);
        } catch (IOException e) {
            throw new HashEncodingException(e);
        }
    }

    public static String hash(GitObject source) {
        try {
            return hash(source.write().toPath());
        } catch (IOException e) {
            throw new HashEncodingException(e);
        }
    }
}
