package no.edh.zlib;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
