package no.edh.objects;

import no.edh.hashing.SHA1;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GitTree implements GitObject {
    private List<GitObject> objectList;

    @Override
    public Path objectPath() throws IOException {
        String hash = sha1().hash();

        return Paths.get(hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));
    }

    @Override
    public SHA1 sha1() throws IOException {
        return new SHA1(this);
    }

    @Override
    public File create() throws IOException {
        File tmpFile = File.createTempFile("foo", "bar");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (GitObject object: objectList){
            baos.write("100644 ".getBytes());
            baos.write(object.getSourceFile().toFile().getName().getBytes());
            baos.write(new byte[1]);
            baos.write(object.sha1().hashBytes());
        }

        RandomAccessFile f = new RandomAccessFile(tmpFile, "rw");
        f.seek(0); // to the beginning

        f.write("tree ".getBytes());
        f.write(String.format("%d", baos.toString().length()).getBytes());
        f.write(new byte[1]);
        f.write(baos.toByteArray());

        f.close();

        return tmpFile;
    }

    @Override
    public Path getSourceFile() {
        return null;
    }

    public void addObjects(List<GitObject> objectList) {
        this.objectList = objectList;
    }
}
