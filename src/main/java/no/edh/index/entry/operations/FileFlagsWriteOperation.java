package no.edh.index.entry.operations;

import no.edh.index.io.WriteOperation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static no.edh.index.entry.operations.misc.BitWiseOperations.intTo12BitByte;

/**
 * (High to low bits)
 * 1 bit: assume-valid/assume-unchanged flag
 * 1-bit: extended flag (must be 0 for versions less than 3; if 1 then an additional 2 bytes follow before the path\file name)
 * 2-bit: merge stage
 * 12-bit: path\file name length (if less than 0xFFF)
 */
public class FileFlagsWriteOperation implements WriteOperation {
    private Path workingFilePath;

    public FileFlagsWriteOperation(Path workingFilePath) {
        this.workingFilePath = workingFilePath;
    }

    @Override
    public long write(RandomAccessFile file) {
        byte[] bytes = intTo12BitByte(workingFilePath.toFile().getName().length());

        int firstFourBitHeaders = 0; // for now we don't support merge, assume-unchanged, etc.
        bytes[0] = (byte) (bytes[0] | ((byte)firstFourBitHeaders));

        try {
            file.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes.length;
    }
}
