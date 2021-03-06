package no.edh.index.entry.effects.misc;

import java.nio.ByteBuffer;

public class BitWiseOperations {
    public static byte[] longToBytes(long unixTime) {
        return new byte[]{
                (byte) (unixTime >> 24),
                (byte) (unixTime >> 16),
                (byte) (unixTime >> 8),
                (byte) unixTime

        };
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getInt();
    }

    public static byte intToByte(int integer) {
        return (byte) integer;
    }

    /**
     * Convert integer to byte, leaving the first 4 bits of the first byte zero-padded
     *
     * @param integer
     * @return
     */
    public static byte[] intTo12BitByte(int integer) {
        return new byte[] {
                (byte) ((integer >> 8) & 15), // bitwise AND with 0000 1111, we're not interested in the first four bits
                (byte) (integer)
        };
    }

    public static int bytesTo12BitInt(byte[] bytes) {
        // TODO: Doesn't at the moment support first 4 bits.
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getInt();
    }

    public static byte[] merge(int bit4, byte[] int12bit) {
        int firstFourBitHeaders = 0; // for now we don't support merge, assume-unchanged, etc.
        int12bit[0] = (byte) (int12bit[0] | ((byte)firstFourBitHeaders));
        return int12bit;
    }
}
