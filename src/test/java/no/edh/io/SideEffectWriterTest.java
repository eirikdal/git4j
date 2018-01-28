package no.edh.io;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SideEffectWriterTest {

    @Rule
    private TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_apply_side_effects() throws IOException {
        temporaryFolder.create();
        SideEffectWriter sideEffectWriter = new SideEffectWriter(temporaryFolder.newFile().toPath());
        long written = sideEffectWriter.apply(0, Stream.of(new SideEffect<RandomAccessFile>() {
            @Override
            public long apply(RandomAccessFile file) {
                try {
                    file.write("test".getBytes());
                    return file.length();
                } catch (IOException e) {
                    throw new SideEffectException("foobar", e);
                }
            }
        }));
        assertEquals(written, 4);
    }
}