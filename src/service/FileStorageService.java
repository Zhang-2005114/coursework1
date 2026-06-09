import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileStorageService {
    public static final String DEFAULT_SAVE_PATH = "data/save.dat";

    private static final String SECTION_PLAYERS = "[PLAYERS]";
    private static final String SECTION_HEROES = "[HEROES]";
    private static final String SECTION_EQUIPMENT = "[EQUIPMENT]";
    private static final String SECTION_TEAMS = "[TEAMS]";
    private static final String SECTION_MATCHES = "[MATCHES]";
    private static final String SECTION_ADMINS = "[ADMINS]";

    private final String savePath;

    public FileStorageService() {
        this(DEFAULT_SAVE_PATH);
    }

    public FileStorageService(String savePath) {
        this.savePath = savePath;
    }

    public String getSavePath() {
        return savePath;
    }

    public boolean saveFileExists() {
        return Files.exists(Paths.get(savePath));
    }

    public void saveAll(GameDataManager data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("GameDataManager cannot be null.");
        }

        Path path = Paths.get(savePath);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# Honor of Kings IMS save file");
            writer.newLine();
            writeSection(writer, SECTION_PLAYERS, collectPlayerLines(data));
            writeSection(writer, SECTION_HEROES, collectHeroLines(data));
            writeSection(writer, SECTION_EQUIPMENT, collectEquipmentLines(data));
            writeSection(writer, SECTION_TEAMS, collectTeamLines(data));
            writeSection(writer, SECTION_MATCHES, collectMatchLines(data));
            writeSection(writer, SECTION_ADMINS, collectAdminLines(data));
        }
    }

    public GameDataManager loadAll() throws IOException {
        Path path = Paths.get(savePath);
        if (!Files.exists(path)) {
            throw new IOException("Save file not found: " + savePath);
        }

        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        Map<String, List<String>> sections = parseSections(allLines);

        GameDataManager data = new GameDataManager();
        loadEquipment(data, sections.getOrDefault(SECTION_EQUIPMENT, List.of()));
        loadHeroes(data, sections.getOrDefault(SECTION_HEROES, List.of()));
        loadPlayers(data, sections.getOrDefault(SECTION_PLAYERS, List.of()));
        loadTeams(data, sections.getOrDefault(SECTION_TEAMS, List.of()));
        loadAdmins(data, sections.getOrDefault(SECTION_ADMINS, List.of()));
        loadMatches(data, sections.getOrDefault(SECTION_MATCHES, List.of()));
        refreshTeamStats(data);
        return data;
    }

    public List<String> collectPlayerLines(GameDataManager data) {
        List<String> lines = new ArrayList<>();
        for (Player player : data.getAllPlayers()) {
            lines.add(player.toCsvLine());
        }
        return lines;
    }

    public void loadPlayers(GameDataManager data, List<String> lines) {
        for (String line : lines) {
            Player player = Player.fromCsvLine(line);
            if (player != null) {
                data.addPlayer(player);
            }
        }
    }

    public List<String> collectHeroLines(GameDataManager data) {
        List<String> lines = new ArrayList<>();
        for (Hero hero : data.getAllHeroes()) {
            lines.add(hero.toCsvLine());
        }
        return lines;
    }

    public void loadHeroes(GameDataManager data, List<String> lines) {
        for (String line : lines) {
            Hero hero = Hero.fromCsvLine(line);
            if (hero != null) {
                data.addHero(hero);
            }
        }
    }

    public List<String> collectEquipmentLines(GameDataManager data) {
        List<String> lines = new ArrayList<>();
        for (Equipment equipment : data.getAllEquipments()) {
            lines.add(equipment.toCsvLine());
        }
        return lines;
    }

    public void loadEquipment(GameDataManager data, List<String> lines) {
        for (String line : lines) {
            Equipment equipment = Equipment.fromCsvLine(line);
            if (equipment != null) {
                data.addEquipment(equipment);
            }
        }
    }

    public List<String> collectTeamLines(GameDataManager data) {
        List<String> lines = new ArrayList<>();
        for (Team team : data.getAllTeams()) {
            lines.add(team.toCsvLine());
        }
        return lines;
    }

    public void loadTeams(GameDataManager data, List<String> lines) {
        for (String line : lines) {
            Team team = Team.fromCsvLine(line);
            if (team != null) {
                data.addTeam(team);
            }
        }
    }

    public List<String> collectMatchLines(GameDataManager data) {
        List<String> lines = new ArrayList<>();
        for (MatchRecord record : data.getAllMatchRecords()) {
            lines.add(record.toCsvLine());
        }
        return lines;
    }

    public void loadMatches(GameDataManager data, List<String> lines) {
        for (String line : lines) {
            MatchRecord record = MatchRecord.fromCsvLine(line);
            if (record != null) {
                data.addMatchRecord(record);
            }
        }
    }

    public List<String> collectAdminLines(GameDataManager data) {
        List<String> lines = new ArrayList<>();
        for (Admin admin : data.getAllAdmins()) {
            lines.add(adminToCsvLine(admin));
        }
        return lines;
    }

    public void loadAdmins(GameDataManager data, List<String> lines) {
        for (String line : lines) {
            Admin admin = adminFromCsvLine(line);
            if (admin != null) {
                data.addAdmin(admin);
            }
        }
    }

    private void writeSection(BufferedWriter writer, String header, List<String> lines)
            throws IOException {
        writer.newLine();
        writer.write(header);
        writer.newLine();
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }

    private Map<String, List<String>> parseSections(List<String> allLines) {
        Map<String, List<String>> sections = new LinkedHashMap<>();
        String current = null;
        for (String line : allLines) {
            if (line == null) {
                continue;
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                current = trimmed;
                sections.putIfAbsent(current, new ArrayList<>());
                continue;
            }
            if (current != null) {
                sections.get(current).add(trimmed);
            }
        }
        return sections;
    }

    private void refreshTeamStats(GameDataManager data) {
        List<Player> players = data.getAllPlayers();
        List<MatchRecord> matches = data.getAllMatchRecords();
        for (Team team : data.getAllTeams()) {
            team.recalculateStats(players, matches);
        }
    }

    private static String adminToCsvLine(Admin admin) {
        return String.join("|",
                String.valueOf(admin.getId()),
                admin.getName() != null ? admin.getName() : "",
                admin.getPassword() != null ? admin.getPassword() : "",
                "Admin");
    }

    private static Admin adminFromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) {
            return null;
        }
        return new Admin(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim());
    }
}
