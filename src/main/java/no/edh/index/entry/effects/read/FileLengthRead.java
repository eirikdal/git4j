package no.edh.index.entry.effects.read;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.index.entry.effects.misc.BitWiseOperations;
import no.edh.index.io.SideEffect;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class FileLengthRead implements SideEffect {
    private Consumer<Long> consumer;

    public FileLengthRead(Consumer<Long> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        byte[] bytes = new byte[4];
        try {
            file.read(bytes, 0, 4);
            this.consumer.accept(BitWiseOperations.bytesToLong(bytes));
        } catch (IOException e) {
            throw new SideEffectException("Error writing to index file", e);
        }
        return bytes.length;
    }
}
