package no.edh.hashing.exceptions;

import java.io.IOException;

public class HashEncodingException extends RuntimeException {
    public HashEncodingException(IOException e) {
        super(e);
    }
}
