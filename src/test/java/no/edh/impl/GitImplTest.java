package no.edh.impl;

import no.edh.Repository;
import no.edh.hashing.SHA1;
import no.edh.index.Index;
import no.edh.objects.*;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GitImplTest {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder();

    static {
        try {
            GitImpl git = new GitImpl();
            git.init("testrepo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void should_add_file_to_index() throws IOException {
        temporaryFolder.create();
        File testFolder = temporaryFolder.newFolder("shouldAddFilesToIndex");

        System.setProperty("repo.dir", testFolder.getPath());

        Path file = Files.write(Paths.get(testFolder.getPath(), "testfile"), "test".getBytes());

        GitImpl git = new GitImpl(testFolder.toPath());
        git.init();
        git.add(file);

        assertEquals(testFolder.toPath().relativize(file), git.status().get(0).getPath());
    }

    @Test
    public void should_make_commit() throws IOException {
        temporaryFolder.create();
        File testFolder = temporaryFolder.newFolder("shouldMakeCommits");

        System.setProperty("repo.dir", testFolder.getPath());

        Path file = Files.write(Paths.get(testFolder.getPath(), "testfile"), "test".getBytes());
        Path file2 = Files.write(Paths.get(testFolder.getPath(), "uuperduperfile"), "testtest".getBytes());

        GitImpl git = new GitImpl(testFolder.toPath());
        git.init();
        git.add(file);
        git.add(file2);

        SHA1 commit1sha = git.commit("foobar");

        Path file3 = Files.write(Paths.get(testFolder.getPath(), "yummibear"), "testtesttest".getBytes());
        git.add(file3);

        SHA1 commit2sha = git.commit("Second commit");

        String hash = commit2sha.getHashHex();
        Path path = Paths.get(testFolder.getPath(),".git", "objects", hash.substring(0, 2)).resolve(hash.substring(2, hash.length()));

        Commit commit2obj = Commit.read(path);

        assertEquals("8751501936ee23adfd7fa7e9bc20c34d1ad56ff4", commit2obj.getTree().getHashHex());
        assertEquals(commit1sha.getHashHex(), commit2obj.getParent());
        assertEquals("Random Guy", commit2obj.getAuthor().getName());
        assertEquals("foo@bar.edu", commit2obj.getAuthor().getEmail());
        assertEquals("GitHub", commit2obj.getCommitter().getName());
        assertEquals("noreply@github.com", commit2obj.getCommitter().getEmail());
        assertEquals("Second commit", commit2obj.getCommitMsg());
    }


}