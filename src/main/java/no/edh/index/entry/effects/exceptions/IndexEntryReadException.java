package no.edh.index.entry.effects.exceptions;

import java.io.IOException;

public class IndexEntryReadException extends RuntimeException {
    public IndexEntryReadException(String msg, IOException e) {
        super(msg, e);
    }
}
