public enum HeroType {
    MAGE("Mage"),
    SHOOTER("Shooter"),
    TANK("Tank"),
    JUNGLER("Jungler"),
    SUPPORT("Support");

    private final String displayName;

    HeroType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static HeroType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        for (HeroType type : values()) {
            if (type.name().equals(normalized)
                    || type.displayName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
