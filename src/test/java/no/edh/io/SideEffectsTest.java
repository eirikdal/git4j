package no.edh.io;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SideEffectsTest {

    @Rule
    private final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_apply_side_effects() throws IOException {
        temporaryFolder.create();
        SideEffects sideEffects = new SideEffects(temporaryFolder.newFile().toPath());
        long written = sideEffects.apply(Stream.of(file -> {
            try {
                file.write("test".getBytes());
                return file.length();
            } catch (IOException e) {
                throw new SideEffectException("foobar", e);
            }
        }));
        assertEquals(written, 4);
    }
}