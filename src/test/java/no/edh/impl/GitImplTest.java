package no.edh.impl;

import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.objects.GitBlob;
import no.edh.objects.GitObject;
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

class GitImplTest {

    private static final String userDir = System.getProperty("user.dir");

    @Test
    void skal_kunne_lage_nytt_repo() throws IOException {
        GitImpl git = new GitImpl();
        git.init("testrepo");

        assertTrue(Paths.get(System.getProperty("user.dir"), "testrepo", ".git").toFile().exists());
    }

    @Test
    void skal_kunne_legge_til_nye_filer() throws IOException {
        Paths.get(userDir, "testrepo", ".git", "index").toFile().delete();
        Paths.get(userDir, "testrepo", "testfile").toFile().delete();
        Path file = Files.write(Paths.get(userDir, "testrepo", "testfile"), "test".getBytes());

        GitImpl git = new GitImpl("testrepo");
        git.add(file);

        Repository repository = new Repository();
        GitBlob blob = new GitBlob(file);
        SHA1 sha1 = new SHA1(blob);
        GitObject gitObject = repository.getObjects().find(sha1);

        assertTrue(((GitBlob) gitObject).getLocation().toFile().exists());
    }

}