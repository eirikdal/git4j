package no.edh.io;

public interface SideEffect<T> {
    long apply(T file);
}
