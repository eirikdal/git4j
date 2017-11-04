package no.edh.impl;

import no.edh.Git;
import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.objects.GitBlob;
import no.edh.objects.GitObject;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitImplTest {

    private static final String userDir = System.getProperty("user.dir");

    static {
        try {
            GitImpl git = new GitImpl();
            git.init("testrepo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void skal_kunne_lage_nytt_repo() throws IOException {
        assertTrue(Paths.get(userDir, "testrepo", ".git").toFile().exists());
    }

    @Test
    void skal_kunne_legge_til_nye_filer() throws IOException {
        Paths.get(userDir, "testrepo", ".git", "index").toFile().delete();
        Paths.get(userDir, "testrepo", "testfile").toFile().delete();
        Paths.get(userDir, "testrepo").toFile().mkdirs();
        Path file = Files.write(Paths.get(userDir, "testrepo", "testfile"), "test".getBytes());
        Path file2 = Files.write(Paths.get(userDir, "testrepo", "uuperduperfile"), "testtest".getBytes());
        Path file3 = Files.write(Paths.get(userDir, "testrepo", "yummibear"), "So so so so ..".getBytes());
        Path file4 = Files.write(Paths.get(userDir, "testrepo", "zoramora"), "ASDFSo so so so ..".getBytes());

        GitImpl git = new GitImpl("testrepo");
        git.add(file);
        git.add(file2);
        git.add(file3);
        git.add(file4);

        Repository repository = new Repository();
        GitBlob blob = new GitBlob(file);
        SHA1 sha1 = new SHA1(blob);
        GitObject gitObject = repository.getObjects().find(sha1);

        assertTrue(Paths.get(userDir, "testrepo", ".git", "index").toFile().exists());
    }

    @Test
    public void should_make_commit() throws IOException {
        Paths.get(userDir, "testrepo2", ".git", "index").toFile().delete();
        Paths.get(userDir, "testrepo2", "testfile").toFile().delete();
        Paths.get(userDir, "testrepo2").toFile().mkdirs();
        Path file = Files.write(Paths.get(userDir, "testrepo2", "testfile"), "test".getBytes());
        Path file2 = Files.write(Paths.get(userDir, "testrepo2", "uuperduperfile"), "testtest".getBytes());

        GitImpl git = new GitImpl("testrepo2");
        git.init("testrepo2");
        git.add(file);
        git.add(file2);

        git.commit("foobar");

        Path file3 = Files.write(Paths.get(userDir, "testrepo2", "yummibear"), "testtest".getBytes());
        git.add(file3);
        git.commit("Second commit");

        assertTrue(Paths.get(userDir, "testrepo2", ".git", "index").toFile().exists());
    }
}