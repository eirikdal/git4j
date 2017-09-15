package no.edh.hashing;

import no.edh.hashing.exceptions.HashEncodingException;
import no.edh.objects.GitObject;
import org.apache.commons.codec.digest.DigestUtils;
import sun.security.provider.SHA;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SHA1 {

    private GitObject source;

    public SHA1(GitObject source) {
        this.source = source;
    }

    public String hash() {
        try {
            return DigestUtils.sha1Hex(source.getHashStream());
        } catch (IOException e) {
            throw new HashEncodingException(e);
        }
    }
}
