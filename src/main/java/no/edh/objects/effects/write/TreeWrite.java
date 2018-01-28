package no.edh.objects.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.objects.Blob;
import no.edh.objects.GitObject;
import no.edh.objects.Tree;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class TreeWrite implements SideEffect<RandomAccessFile> {

    private final Tree tree;

    public TreeWrite(Tree tree) {
        this.tree = tree;
    }

    /**
     * In this case, you’re specifying a mode of 100644, which means it’s a normal file.
     * Other options are 100755, which means it’s an executable file; and 120000, which specifies a symbolic link.
     * @return bytes written
     */
    @Override
    public long apply(RandomAccessFile treeOutputFile) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (GitObject object: tree.getObjectList()){
                baos.write("100644 ".getBytes());
                baos.write(object.getSourceFile().toFile().getName().getBytes());
                baos.write(new byte[1]);
                baos.write(object.sha1().hashBytes());
            }

            treeOutputFile.seek(0); // to the beginning

            treeOutputFile.write("tree ".getBytes());
            treeOutputFile.write(String.format("%d", baos.toString().length()).getBytes());
            treeOutputFile.write(new byte[1]);
            treeOutputFile.write(baos.toByteArray());

            return treeOutputFile.length();
        } catch (IOException e) {
            throw new SideEffectException("Failed to write side effect to tree object", e);
        }
    }
}
