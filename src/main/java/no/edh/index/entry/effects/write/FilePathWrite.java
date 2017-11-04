package no.edh.index.entry.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.io.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathWrite implements SideEffect {

    private static final Logger logger = LoggerFactory.getLogger(FilePathWrite.class);

    private Path path;

    public FilePathWrite(Path path) {
        this.path = path;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            URI testrepo = Paths.get(System.getProperty("repo.dir")).toUri();
            URI fileUri = path.toFile().toURI();

            URI relativize = testrepo.relativize(fileUri);

            byte[] bytes = relativize.getPath().getBytes();
            file.write(bytes);
            return bytes.length;
        } catch (IOException e) {
            logger.error("Error writing file path entry", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}
