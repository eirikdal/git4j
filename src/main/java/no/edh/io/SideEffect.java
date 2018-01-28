package no.edh.io;

import java.io.RandomAccessFile;

public interface SideEffect<T> {
    public long apply(T file);
}
