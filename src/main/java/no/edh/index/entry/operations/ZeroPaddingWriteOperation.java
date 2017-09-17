package no.edh.index.entry.operations;

import no.edh.index.entry.operations.exceptions.WriteOperationException;
import no.edh.index.io.WriteOperation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class ZeroPaddingWriteOperation implements WriteOperation {
    private Path workingFilePath;
    private static final int minPathLength = 0xfff;

    public ZeroPaddingWriteOperation(Path workingFilePath) {
        this.workingFilePath = workingFilePath;
    }

    @Override
    public long write(RandomAccessFile file) {
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
            throw new WriteOperationException("Failed to write zero-padding", e);
        }
    }

    private int getPadLen() throws IOException {
        final int actLen = 62 + workingFilePath.toFile().getName().length();
        final int expLen = (actLen + 8) & ~7;
        final int padLen = expLen - actLen - 0; // TODO: we don't support extended file names yet..
        return padLen;
    }
}
