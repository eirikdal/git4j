package no.edh.index.header.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HeaderWrite implements SideEffect {
    private static final Logger logger = LoggerFactory.getLogger(HeaderWrite.class);

    private static final int OFFSET = 0;

    private final String header;

    public HeaderWrite(String header) {
        this.header = header;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            file.seek(OFFSET);
            file.writeBytes(header);
            return file.getFilePointer();
        } catch (IOException e) {
            logger.warn("Error writing index header", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
