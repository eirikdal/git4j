package no.edh;

import no.edh.hashing.SHA1;
import no.edh.index.Index;
import no.edh.index.entry.IndexEntry;
import no.edh.index.ops.CacheInfo;
import no.edh.index.ops.UpdateIndex;
import no.edh.objects.GitObject;
import no.edh.objects.Objects;
import no.edh.objects.Tree;

import java.io.IOException;
import java.util.List;

public class Plumbing {

    private final Repository repository;

    public Plumbing(Repository repository) {
        this.repository = repository;
    }

    public String catFile() {
        return "";
    }

    public Tree writeTree() {
        return null;
    }

    public void updateIndex(UpdateIndex type, CacheInfo cacheInfo) throws IOException {
        Index index = repository.getIndex();

        List<IndexEntry> entries = index.readEntries();
        index.removeEntries();

        if (UpdateIndex.ADD.equals(type)) {
            List<CacheInfo> objects = Objects.map(entries);
            Objects.sort(objects);
            objects.add(cacheInfo);
            objects.forEach(index::addBlobToIndex);
            index.updateIndex();
        } else if (UpdateIndex.REMOVE.equals(type)) {
            List<CacheInfo> objects = Objects.map(entries);
            Objects.sort(objects);
            objects.remove(cacheInfo);
            objects.forEach(index::addBlobToIndex);
            index.updateIndex();
        }
    }

    public void readTree() {

    }

    public void updateRef() {

    }

    public SHA1 hashObject(GitObject object, boolean writeToObjects) throws IOException {
        if (writeToObjects) {
            repository.getObjects().writeObject(object);
        }

        return object.sha1();
    }
}
