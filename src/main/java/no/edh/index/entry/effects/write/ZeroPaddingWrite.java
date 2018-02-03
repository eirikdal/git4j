package no.edh.index.entry.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class ZeroPaddingWrite implements SideEffect<RandomAccessFile> {
    private final Path workingFilePath;
    private static final int minPathLength = 0xfff;

    public ZeroPaddingWrite(Path workingFilePath) {
        this.workingFilePath = workingFilePath;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            int padLen = getPadLen();
            if (padLen > 0) {
                byte[] zeroPadding = new byte[padLen];
                file.write(zeroPadding);
            } else if (padLen == 0) { // no padding necessary, still need to zero-terminate tho..
                file.write(new byte[1]);
                padLen++;
            }
            return (long) padLen;
        } catch (IOException e) {
            throw new SideEffectException("Failed to write zero-padding", e);
        }
    }

    private int getPadLen() {
        final int actLen = 62 + workingFilePath.toFile().getName().length();
        final int expLen = (actLen + 8) & ~7;
        return expLen - actLen;
    }
}
