package enums;


public enum EquipmentType {
    ATTACK("Attack"),
    DEFENSE("Defense"),
    SPELL("Spell");

    private final String displayName;

    EquipmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EquipmentType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        for (EquipmentType type : values()) {
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
