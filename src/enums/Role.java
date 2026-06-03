public enum Role {
    ADMIN("Admin"),
    PLAYER("Player");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static Role fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        for (Role role : values()) {
            if (role.name().equals(normalized)
                    || role.roleName.equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        return null;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isPlayer() {
        return this == PLAYER;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
