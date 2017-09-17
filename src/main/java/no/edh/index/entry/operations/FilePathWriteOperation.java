package no.edh.index.entry.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.io.WriteOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathWriteOperation implements WriteOperation {

    private static final Logger logger = LoggerFactory.getLogger(FilePathWriteOperation.class);

    private Path path;

    public FilePathWriteOperation(Path path) {
        this.path = path;
    }

    @Override
    public long write(RandomAccessFile file) {
        try {
            URI testrepo = Paths.get(System.getProperty("user.dir"), "testrepo").toUri();
            URI fileUri = path.toFile().toURI();

            URI relativize = testrepo.relativize(fileUri);

            byte[] bytes = relativize.getPath().getBytes();
            file.write(bytes);
            return bytes.length;
        } catch (IOException e) {
            logger.error("Error writing file path entry", e);
            throw new WriteOperationException("Error writing to index file", e);
        }
    }
}
