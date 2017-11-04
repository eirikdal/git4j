package no.edh.index.entry.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.file.FileAttr;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileAttrWrite implements SideEffect {

    private static final Logger logger = LoggerFactory.getLogger(FileAttrWrite.class);

    private FileAttr attr;

    public FileAttrWrite(FileAttr attr) {
        this.attr = attr;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            file.write(attr.bytes());
            return attr.bytes().length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
