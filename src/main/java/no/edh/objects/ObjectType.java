package no.edh.objects;

public enum ObjectType {
    Commit("commit"),
    Blob("blob"),
    Tree("tree");

    private final String type;

    ObjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
