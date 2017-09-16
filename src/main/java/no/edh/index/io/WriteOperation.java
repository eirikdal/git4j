package no.edh.index.io;

import java.io.RandomAccessFile;

public interface WriteOperation {
    public long write(RandomAccessFile file);
}
