public enum MatchResult {
    WIN("Win"),
    LOSE("Lose");

    private final String displayName;

    MatchResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MatchResult fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        for (MatchResult result : values()) {
            if (result.name().equals(normalized)
                    || result.displayName.equalsIgnoreCase(value.trim())) {
                return result;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
