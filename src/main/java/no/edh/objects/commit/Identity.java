package no.edh.objects.commit;

public class Identity {

    private final String email;
    private final String name;

    public Identity(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
