package no.edh.index.entry.effects.misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OperationUtilsTest {

    @Test
    public void should_convert_int_to_byte() {
        assertEquals((byte) 8, BitWiseOperations.intToByte(8));
    }

    @Test
    public void should_convert_int_to_12bit() {
        assertArrayEquals(new byte[] {1, 1}, BitWiseOperations.intTo12BitByte(257));
    }

}