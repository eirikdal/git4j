package no.edh.archive.zlib;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibInflater {

    /**
     * Decompresses a zlib compressed file.
     */
    public void decompressFile(Path compressed, OutputStream out)
            throws IOException
    {
        InputStream in =
                new InflaterInputStream(Files.newInputStream(compressed));
        IOUtils.copy(in, out);
        in.close();
    }
}
