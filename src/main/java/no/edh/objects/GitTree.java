package no.edh.objects;

import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import no.edh.hashing.SHA1;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static no.edh.index.entry.operations.misc.BitWiseOperations.longToBytes;

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
//
//    100644 blob 5f2dbe11df91c879164ae1150f5577ac7263c1da    .gitignore
//100644 blob 4b12d1c1925bcf262e4639cf3c8be91b789fbed3    LICENSE
//100644 blob f8905c6dd205324330419cedbb57bf87d5a9cbda    README.md

    @Override
    public File create() throws IOException {
        File tmpFile = File.createTempFile("foo", "bar");

        ByteArrayOutputStream baos = new ByteArrayOutputStreamEx();
        for (GitObject object: objectList){
            baos.write("10644 ".getBytes());
            baos.write(object.getSourceFile().toFile().getName().getBytes());
            baos.write(" ".getBytes());
            baos.write(object.sha1().hashBytes());
            baos.write(new byte[4]);
        }

        RandomAccessFile f = new RandomAccessFile(tmpFile, "rw");
        f.seek(0); // to the beginning

        f.write("tree ".getBytes());
        f.write(baos.toByteArray().length);
        f.write(0);
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
