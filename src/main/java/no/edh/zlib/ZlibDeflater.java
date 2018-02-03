package no.edh.zlib;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibDeflater {

    /**
     * Compresses a file with zlib compression.
     */
    public void compress(File file, File out)
            throws IOException
    {
        try (
                FileInputStream in = new FileInputStream(file);
                FileOutputStream fileOutputStream = new FileOutputStream(out);
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream)
        ) {
            IOUtils.copy(in, deflaterOutputStream);
        }
    }
}
