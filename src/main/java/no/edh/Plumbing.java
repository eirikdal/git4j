package no.edh;

import no.edh.archive.Archive;
import no.edh.hashing.SHA1;
import no.edh.objects.GitObject;

import java.io.IOException;

public class Plumbing {

    public String catFile() {
        return "";
    }

    public SHA1 hashObject(GitObject object, boolean writeToObjects) throws IOException {
        Archive archive = new Archive(object.objectPath());

        return new SHA1(object);
    }
}
