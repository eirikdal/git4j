package no.edh.index.entry.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.file.FileAttr;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileAttrWriteOperation implements WriteOperation {

    private static final Logger logger = LoggerFactory.getLogger(FileAttrWriteOperation.class);

    private FileAttr attr;

    public FileAttrWriteOperation(FileAttr attr) {
        this.attr = attr;
    }

    @Override
    public long write(RandomAccessFile file) {
        try {
            file.write(attr.bytes());
            return attr.bytes().length;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
