package no.edh.objects.effects.write;

import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.objects.Commit;

import java.io.*;

public class CommitWrite implements SideEffect<RandomAccessFile> {

    private final Commit commit;

    public CommitWrite(Commit commit) {
        this.commit = commit;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            StringWriter baos = new StringWriter();
            baos.write("tree ");
            baos.write(commit.getTree().getHashHex());
            baos.write("\n");
            if (commit.getParent() != null) {
                baos.write("parent ");
                baos.write(commit.getParent());
                baos.write("\n");
            }
            baos.write("author Random Guy <foo@bar.edu> ");
            baos.write(String.format("%d", commit.getTime()));
            baos.write(" +0200\n");
            baos.write("committer GitHub <noreply@github.com> ");
            baos.write(String.format("%d", commit.getTime()));
            baos.write(" +0200\n\n");
            baos.write(commit.getCommitMsg());
            baos.write("\n\n");

            file.seek(0); // to the beginning
            file.write("commit ".getBytes());
            file.write(String.format("%d", baos.toString().length()).getBytes());
            file.write(new byte[] {0});
            file.write(baos.toString().getBytes());

            return file.length();
        } catch (IOException e) {
            throw new SideEffectException("Error writing to object file", e);
        }
    }
}
