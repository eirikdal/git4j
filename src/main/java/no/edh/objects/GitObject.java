package no.edh.objects;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface GitObject {
    InputStream getHashStream() throws IOException;
    InputStream getContentStream();
}
