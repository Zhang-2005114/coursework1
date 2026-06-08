public class AuthenticationService {
    private final GameDataManager dataManager;
    private Person currentUser;

    public AuthenticationService(GameDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public boolean login(int id, String password) {
        if (currentUser != null) {
            System.out.println("Already logged in as " + currentUser.getName()
                    + ". Please logout first.");
            return false;
        }

        Person user = findPersonById(id);
        if (user == null) {
            System.out.println("User not found: ID " + id);
            return false;
        }
        return authenticateAndSetUser(user, password);
    }

    public boolean login(String name, String password) {
        if (currentUser != null) {
            System.out.println("Already logged in as " + currentUser.getName()
                    + ". Please logout first.");
            return false;
        }
        if (name == null || name.isBlank()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        Person user = findPersonByName(name.trim());
        if (user == null) {
            System.out.println("User not found: " + name.trim());
            return false;
        }
        return authenticateAndSetUser(user, password);
    }

    public void logout() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        currentUser.logout();
        currentUser = null;
    }

    public Person getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean isPlayer() {
        return currentUser != null && currentUser.isPlayer();
    }

    public boolean requireAdmin() {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return false;
        }
        if (!isAdmin()) {
            System.out.println("Access denied. Admin privileges required.");
            return false;
        }
        return true;
    }

    private boolean authenticateAndSetUser(Person user, String password) {
        if (password == null || password.isBlank()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        if (!user.authenticate(password)) {
            System.out.println("Invalid password.");
            return false;
        }

        currentUser = user;
        System.out.println("Login successful. Welcome, " + user.getName()
                + " (" + user.getRole() + ").");
        return true;
    }

    private Person findPersonById(int id) {
        Admin admin = dataManager.getAdminById(id);
        if (admin != null) {
            return admin;
        }
        return dataManager.getPlayerById(id);
    }

    private Person findPersonByName(String name) {
        for (Admin admin : dataManager.getAllAdmins()) {
            if (admin.getName().equalsIgnoreCase(name)) {
                return admin;
            }
        }
        for (Player player : dataManager.getAllPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
}
