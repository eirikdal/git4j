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
            file.write(new byte[] {0});
            int padLen = getPadLen(file);
            if (padLen > 0) {
                byte[] zeroPadding = new byte[padLen];
                file.write(zeroPadding);
            }
            return (long) padLen + 1;
        } catch (IOException e) {
            throw new WriteOperationException("Failed to write zero-padding", e);
        }
    }

    private int getPadLen(RandomAccessFile file) throws IOException {
        return (int) (workingFilePath.toFile().getName().length()) % 8;
//        final int expLen = (int) (file.getFilePointer (workingFilePath.toFile().getName().length()+1));
//        return (int) (file.getFilePointer() - expLen) % 8;
    }
}
