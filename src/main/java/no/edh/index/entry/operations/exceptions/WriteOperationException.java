package no.edh.index.entry.operations.exceptions;

import java.io.IOException;

public class WriteOperationException extends RuntimeException {
    public WriteOperationException(String message, Exception e) {
        super(message, e);
    }
}
