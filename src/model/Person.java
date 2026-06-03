public abstract class Person implements Authenticatable{
    private int id; // user id
    private String name;
    private String password;
    private String role; // Admin/Player

    public Person(){

    }

    public Person(int id,String name, String password, String role){
        this.id=id;
        this.name=name;
        this.password=password;
        this.role=role;
    }

    public int getId(){
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean authenticate(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public boolean login(String inputPassword){
        return authenticate(inputPassword);
    }

    public void logout() {
        System.out.println("User: " + this.name + " has logged out.");
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean isPlayer() {
        return "Player".equalsIgnoreCase(role);
    }

    public String getInfo() {
        return "User information [ID: " + id + ", name: " + name + ", role: " + role + "]";
    }
}
