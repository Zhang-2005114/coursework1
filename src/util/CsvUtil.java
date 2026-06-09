import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class CsvUtil {
    private CsvUtil() {
    }

    public static String joinInts(List<Integer> values, String delimiter) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(delimiter);
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }

    public static List<Integer> parseIntList(String text, String delimiter) {
        List<Integer> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        for (String part : text.split(Pattern.quote(delimiter))) {
            if (!part.isBlank()) {
                result.add(Integer.parseInt(part.trim()));
            }
        }
        return result;
    }

    public static String encodeStats(Map<String, Integer> stats) {
        if (stats == null || stats.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            if (builder.length() > 0) {
                builder.append(";");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }

    public static Map<String, Integer> decodeStats(String text) {
        Map<String, Integer> stats = new HashMap<>();
        if (text == null || text.isBlank()) {
            return stats;
        }
        for (String pair : text.split(";")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && !kv[0].isBlank()) {
                stats.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
            }
        }
        return stats;
    }

    public static String encodeEquipped(Map<Integer, List<Integer>> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(entry.getKey()).append(":").append(joinInts(entry.getValue(), "."));
        }
        return builder.toString();
    }

    public static Map<Integer, List<Integer>> decodeEquipped(String text) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        if (text == null || text.isBlank()) {
            return map;
        }
        for (String group : text.split(",")) {
            String[] pair = group.split(":", 2);
            if (pair.length == 2) {
                map.put(Integer.parseInt(pair[0].trim()), parseIntList(pair[1], "."));
            }
        }
        return map;
    }

    public static boolean matchesIdOrName(int id, String name, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        String trimmed = keyword.trim();
        if (trimmed.equals(String.valueOf(id))) {
            return true;
        }
        return name != null && name.toLowerCase().contains(trimmed.toLowerCase());
    }
}
