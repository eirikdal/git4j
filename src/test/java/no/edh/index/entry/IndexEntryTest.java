package no.edh.index.entry;

import no.edh.objects.Blob;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class IndexEntryTest {

    public static final int ENTRY_LENGTH = 64;

    @Rule
    private TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_write_index_entry() throws IOException {
        temporaryFolder.create();
        File testrepo = temporaryFolder.newFolder("testrepo");
        System.setProperty("user.dir", temporaryFolder.getRoot().getPath());
        File testfile = temporaryFolder.newFile("testrepo/testfile");
        File index = temporaryFolder.newFile("testrepo/index");

        Blob blob = new Blob(testfile.toPath());
        IndexEntry entry = new IndexEntry(index.toPath(), blob, 0);
        entry.write();
        assertEquals(ENTRY_LENGTH + testfile.getName().length(), Files.readAllBytes(index.toPath()).length);
    }
}