public interface Authenticatable {

    boolean login(String password);
    void logout();
    boolean authenticate(String password);

}
