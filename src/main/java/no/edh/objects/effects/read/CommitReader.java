package no.edh.objects.effects.read;

import no.edh.hashing.SHA1;
import no.edh.index.entry.effects.exceptions.SideEffectException;
import no.edh.io.SideEffect;
import no.edh.objects.Commit;
import no.edh.objects.commit.Author;
import no.edh.objects.commit.Committer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Consumer;

public class CommitReader implements SideEffect<RandomAccessFile> {

    private static final Logger logger = LoggerFactory.getLogger(CommitReader.class);

    private Consumer<Commit> consumer;

    public CommitReader(Consumer<Commit> consumer) {
        this.consumer = consumer;
    }

    @Override
    public long apply(RandomAccessFile file) {
        try {
            Commit commit = new Commit();

            String test = file.readLine();
            String tree = test.substring(test.indexOf("tree ")+5, test.length());
            commit.setTree(new SHA1(tree));
            String parent = file.readLine();;

            parent = parent.substring(parent.indexOf("parent ") + 7, parent.length());
            commit.setParent(parent);

            String tmp = file.readLine();
            String author = tmp.substring(tmp.indexOf("author ") + 7, tmp.indexOf("<") -1);
            String email = tmp.substring(tmp.indexOf("<")+1, tmp.indexOf(">"));
            commit.setAuthor(new Author(email, author));

            String timestamp = tmp.substring(tmp.indexOf(">")+2, tmp.indexOf("+")-1);
            commit.setTime(Long.valueOf(timestamp));

            String tmp2 = file.readLine();
            String committer = tmp2.substring(tmp2.indexOf("committer ") + 10, tmp2.indexOf("<") -1);
            String committerEmail = tmp2.substring(tmp2.indexOf("<")+1, tmp2.indexOf(">"));
            commit.setCommitter(new Committer(committerEmail, committer));

            file.readLine();
            String commitMessage = file.readLine();
            commit.setCommitMsg(commitMessage);

            this.consumer.accept(commit);
            return 0;
        } catch (IOException e) {
            logger.error("Error writing file attributes", e);
            throw new SideEffectException("Error writing to index file", e);
        }
    }
}