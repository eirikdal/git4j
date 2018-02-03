package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ZeroPaddingRead implements SideEffect<RandomAccessFile> {

    private static Logger logger = LoggerFactory.getLogger(ZeroPaddingRead.class);

    @Override
    public long apply(RandomAccessFile fos) {
        long filePointer, read = 0;
        try {
            filePointer = fos.getFilePointer();
            while (fos.readByte() == 0) {
                fos.seek(filePointer);
                filePointer++;
                read++;
            }
            fos.seek(filePointer-1);
        } catch (IOException e) {
            throw new SideEffectException("Error writing to index file", e);
        }

        return read-1;
    }
}
