package no.edh.hashing;

import no.edh.hashing.exceptions.HashEncodingException;
import no.edh.objects.GitObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class SHA1 {

    private GitObject source;

    public SHA1(GitObject source) {
        this.source = source;
    }

    public byte[] hashBytes() {
        try (FileInputStream inputStream = new FileInputStream(source.create())) {
            return DigestUtils.sha1(inputStream);
        } catch (IOException e) {
            throw new HashEncodingException(e);
        }
    }

    public String hash() {
        try (FileInputStream inputStream = new FileInputStream(source.create())) {
            return DigestUtils.sha1Hex(inputStream);
        } catch (IOException e) {
            throw new HashEncodingException(e);
        }
    }
}
