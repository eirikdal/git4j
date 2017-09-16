package no.edh.index.file;

public class FileAttr {
    private byte[] bytes;

    /**
     * TODO: Figure out how to get the attributes we want.. for now, hardcode this
     *
     * @param bytes
     */
    public FileAttr(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] bytes() {
        return this.bytes;
    }
}
