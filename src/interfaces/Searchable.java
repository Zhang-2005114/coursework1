public interface Searchable {
    int getId();

    String getName();

    boolean matches(String keyword);
}
