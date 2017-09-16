package no.edh.objects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class GitCommit implements GitObject{

    private String commitMsg;

    public GitCommit(String commitMsg) {
        this.commitMsg = commitMsg;
    }

    @Override
    public Path getObjectPath() {
        return null;
    }

    @Override
    public InputStream getHashStream() throws IOException {
        return null;
    }

    @Override
    public InputStream getContentStream() {
        return null;
    }
}
