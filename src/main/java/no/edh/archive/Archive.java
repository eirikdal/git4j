package no.edh.archive;

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

    public Archive(InputStream contentStream) throws IOException {
        new ZlibInflater().compress(contentStream, Files.newOutputStream(this.getArchive()));
    }

    public Path getArchive() {
        return archive;
    }
}
