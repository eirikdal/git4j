package no.edh.index.entry.operations;

public class OperationUtils {
    public static byte[] longToBytes(long unixTime) {
        return new byte[]{
                (byte) (unixTime >> 24),
                (byte) (unixTime >> 16),
                (byte) (unixTime >> 8),
                (byte) unixTime

        };
    }
}
