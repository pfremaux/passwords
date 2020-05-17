package passwords.pojo;

public final class CredentialDatum {
    public static final int NBR_FIELDS = CredentialDatum.class.getDeclaredFields().length;
    private final String hierarchy;
    private final String url;
    private final String login;
    private final String password;
    private final String comments;

    public CredentialDatum(String hierarchy, String url, String login, String password, String comments) {
        this.hierarchy = hierarchy;
        this.url = url;
        this.login = login;
        this.password = password;
        this.comments = comments;
    }

    public String getUrl() {
        return url;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getComments() {
        return comments;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public CredentialDatum move(String newHierarchy) {
        return new CredentialDatum(newHierarchy, url, login, password, comments);
    }

    @Override
    public String toString() {
        return this.login + " @ " + this.url;
    }

    public String getDisplayableInfo() {
        return login + " : " + url;
    }
}
