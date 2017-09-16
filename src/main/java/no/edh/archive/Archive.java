package no.edh.archive;

import no.edh.archive.zlib.ZlibDeflater;
import no.edh.archive.zlib.ZlibInflater;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Archive {

    private Path archive;

    public Archive(Path archive) throws IOException {
        archive = Files.createTempFile("archive", "object");
        archive.toFile().deleteOnExit();
    }

    public Archive(File file) throws IOException {
        new ZlibDeflater().compress(file, this.getArchive().toFile());
    }

    public Path getArchive() {
        return archive;
    }
}
