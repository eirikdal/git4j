package no.edh.archive.zlib;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibInflater {

    /**
     * Compresses a file with zlib compression.
     */
    public void compress(InputStream in, OutputStream out)
            throws IOException
    {
        out = new DeflaterOutputStream(out);
        IOUtils.copy(in, out);
        in.close();
    }
}
