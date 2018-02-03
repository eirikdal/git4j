package no.edh.zlib;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

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
