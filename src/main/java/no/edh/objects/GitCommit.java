package no.edh.objects;

import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import no.edh.hashing.SHA1;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static no.edh.index.entry.operations.misc.BitWiseOperations.longToBytes;

public class GitCommit implements GitObject{

    private final long time;
    private GitTree tree;
    private String commitMsg;

    public GitCommit(GitTree tree, String commitMsg) {
        this.tree = tree;
        this.commitMsg = commitMsg;
        this.time = System.currentTimeMillis();
    }

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

        StringWriter baos = new StringWriter();
        baos.write("tree ");
        baos.write(tree.sha1().hash());
        baos.write("\n");
        baos.write("author Eirik Daleng Haukedal <eirik.haukedal@gmail.com> ");
        baos.write(String.format("%d", time));
        baos.write(" +0200\n");
        baos.write("committer GitHub <noreply@github.com> ");
        baos.write(String.format("%d", time));
        baos.write(" +0200\n\n");
        baos.write(commitMsg);
        baos.write("\n\n");
        
        RandomAccessFile f = new RandomAccessFile(tmpFile, "rw");
        f.seek(0); // to the beginning
        f.write("commit ".getBytes());
        f.write(String.format("%d", baos.toString().length()).getBytes());
        f.write(new byte[] {0});
        f.write(baos.toString().getBytes());
        f.close();

        return tmpFile;
    }
//    commit 286tree 6c92feea55a836de49c46106e35a5d13f97d5a5b
//    parent 9f82927328f71645a9b8499293317458c19429b3
//    author Eirik Daleng Haukedal <eirik.haukedal@gmail.no> 1505640052 +0200
//    committer Eirik Daleng Haukedal <eirik.haukedal@gmail.no> 1505640052 +0200
//
//    Zero-padding finally working properly now..

    @Override
    public Path getSourceFile() {
        return null;
    }
}
