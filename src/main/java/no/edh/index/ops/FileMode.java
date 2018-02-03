package no.edh.index.ops;

public enum FileMode {
    Blob("100644");

    private final String fileMode;

    FileMode(String fileMode) {
        this.fileMode = fileMode;
    }

    public String getFileMode() {
        return fileMode;
    }
}
