package no.edh.index.entry.effects.exceptions;

public class SideEffectException extends RuntimeException {
    public SideEffectException(String message, Exception e) {
        super(message, e);
    }
}
