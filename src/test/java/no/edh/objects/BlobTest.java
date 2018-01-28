package no.edh.objects;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.io.SideEffectWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BlobTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_apply_side_effects() throws IOException {
        temporaryFolder.create();
        File file = temporaryFolder.newFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("test");
        }
        Blob blob = new Blob(file.toPath());

        File blobFile = blob.create();

        assertArrayEquals(
                new byte[]{ 'b', 'l', 'o', 'b', ' ', '4', 0, 't', 'e', 's', 't' },
                Files.readAllBytes(blobFile.toPath())
        );
        assertEquals(blobFile.length(), 11);
    }

}