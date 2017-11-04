package no.edh.index.io;

import java.io.RandomAccessFile;

public interface SideEffect {
    public long apply(RandomAccessFile file);
}
