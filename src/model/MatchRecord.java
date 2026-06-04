import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchRecord implements Searchable, Persistable {
    private int id;
    private LocalDate date;
    private int playerId;
    private int teamId;
    private String opponent;
    private MatchResult result;
    private List<Integer> pickedHeroes;

    public MatchRecord() {
        this.pickedHeroes = new ArrayList<>();
    }

    public MatchRecord(int id, LocalDate date, int playerId, int teamId, String opponent,
                       MatchResult result, List<Integer> pickedHeroes) {
        this.id = id;
        this.date = date;
        this.playerId = playerId;
        this.teamId = teamId;
        this.opponent = opponent;
        this.result = result;
        this.pickedHeroes = pickedHeroes != null ? new ArrayList<>(pickedHeroes) : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public MatchResult getResult() {
        return result;
    }

    public void setResult(MatchResult result) {
        this.result = result;
    }

    public List<Integer> getPickedHeroes() {
        return pickedHeroes;
    }

    public void setPickedHeroes(List<Integer> pickedHeroes) {
        this.pickedHeroes = pickedHeroes != null ? new ArrayList<>(pickedHeroes) : new ArrayList<>();
    }

    public void addPickedHero(int heroId) {
        if (!pickedHeroes.contains(heroId)) {
            pickedHeroes.add(heroId);
        }
    }

    public boolean isWin() {
        return result == MatchResult.WIN;
    }

    public String getResultDisplay() {
        if (result == null) {
            return "Unknown";
        }
        return result.getDisplayName();
    }

    @Override
    public String getName() {
        return opponent != null ? opponent : "";
    }

    @Override
    public boolean matches(String keyword) {
        if (CsvUtil.matchesIdOrName(id, getName(), keyword)) {
            return true;
        }
        return pickedHeroes != null && CsvUtil.joinInts(pickedHeroes, ";").contains(keyword.trim());
    }

    @Override
    public String toCsvLine() {
        return String.join("|",
                String.valueOf(id),
                date != null ? date.toString() : "",
                String.valueOf(playerId),
                String.valueOf(teamId),
                opponent != null ? opponent : "",
                result != null ? result.name() : "",
                CsvUtil.joinInts(pickedHeroes, ";"));
    }

    public static MatchRecord fromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) {
            return null;
        }
        MatchRecord record = new MatchRecord();
        record.setId(Integer.parseInt(parts[0].trim()));
        if (!parts[1].isBlank()) {
            record.setDate(LocalDate.parse(parts[1].trim()));
        }
        record.setPlayerId(Integer.parseInt(parts[2].trim()));
        record.setTeamId(Integer.parseInt(parts[3].trim()));
        record.setOpponent(parts[4].trim());
        record.setResult(MatchResult.fromString(parts[5].trim()));
        if (parts.length > 6 && !parts[6].isBlank()) {
            record.setPickedHeroes(CsvUtil.parseIntList(parts[6], ";"));
        }
        return record;
    }

    @Override
    public String toString() {
        return "MatchRecord{id=" + id + ", date=" + date + ", playerId=" + playerId
                + ", teamId=" + teamId + ", opponent='" + opponent + "', result=" + result
                + ", pickedHeroes=" + pickedHeroes + "}";
    }
}
