package no.edh.index;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class IndexTest {

    @Rule
    final
    TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_write_index_header() throws IOException {
        temporaryFolder.create();
        Path indexFile = temporaryFolder.newFile().toPath();

        Index index = new Index(indexFile);
        index.init();

        assertArrayEquals(Files.readAllBytes(indexFile), new byte[] {
                'D', 'I', 'R', 'C', 0, 0, 0, 2, 0, 0, 0, 0
        });
    }

}