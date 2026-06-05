import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataManager {
    private final Map<Integer, Player> players = new HashMap<>();
    private final Map<Integer, Hero> heroes = new HashMap<>();
    private final Map<Integer, Equipment> equipments = new HashMap<>();
    private final Map<Integer, Team> teams = new HashMap<>();
    private final Map<Integer, MatchRecord> matches = new HashMap<>();
    private final Map<Integer, Admin> admins = new HashMap<>();

    //  Player

    public void addPlayer(Player player) {
        if (player != null) {
            players.put(player.getId(), player);
        }
    }

    public boolean deletePlayer(int id) {
        Player removed = players.remove(id);
        if (removed == null) {
            return false;
        }
        for (Team team : teams.values()) {
            team.removePlayer(id);
        }
        for (Hero hero : heroes.values()) {
            hero.getOwnerPlayers().remove(Integer.valueOf(id));
        }
        matches.entrySet().removeIf(e -> e.getValue().getPlayerId() == id);
        return true;
    }

    public Player findPlayerById(int id) {
        return players.get(id);
    }

    public Player findPlayerByName(String name) {
        return findByName(players, name);
    }

    //  Hero

    public void addHero(Hero hero) {
        if (hero != null) {
            heroes.put(hero.getId(), hero);
        }
    }

    public boolean deleteHero(int id) {
        if (!heroes.containsKey(id)) {
            return false;
        }
        for (Player player : players.values()) {
            player.getOwnedHeroes().remove(Integer.valueOf(id));
            player.getEquippedItemsByHeroId().remove(id);
        }
        for (Equipment equipment : equipments.values()) {
            equipment.getCompatibleHeroes().remove(Integer.valueOf(id));
        }
        for (MatchRecord record : matches.values()) {
            record.getPickedHeroes().remove(Integer.valueOf(id));
        }
        heroes.remove(id);
        return true;
    }

    public Hero findHeroById(int id) {
        return heroes.get(id);
    }

    public Hero findHeroByName(String name) {
        return findByName(heroes, name);
    }

    // Equipment

    public void addEquipment(Equipment equipment) {
        if (equipment != null) {
            equipments.put(equipment.getId(), equipment);
        }
    }

    public boolean deleteEquipment(int id) {
        if (!equipments.containsKey(id)) {
            return false;
        }
        for (Hero hero : heroes.values()) {
            hero.getCompatibleEquipments().remove(Integer.valueOf(id));
        }
        for (Player player : players.values()) {
            for (List<Integer> equipped : player.getEquippedItemsByHeroId().values()) {
                equipped.remove(Integer.valueOf(id));
            }
        }
        equipments.remove(id);
        return true;
    }

    public Equipment findEquipmentById(int id) {
        return equipments.get(id);
    }

    public Equipment findEquipmentByName(String name) {
        return findByName(equipments, name);
    }

    // Team

    public void addTeam(Team team) {
        if (team != null) {
            teams.put(team.getId(), team);
        }
    }

    public boolean deleteTeam(int id) {
        Team removed = teams.remove(id);
        if (removed == null) {
            return false;
        }
        for (Player player : players.values()) {
            if (player.getTeamId() == id) {
                player.setTeamId(0);
            }
        }
        matches.entrySet().removeIf(e -> e.getValue().getTeamId() == id);
        return true;
    }

    public Team findTeamById(int id) {
        return teams.get(id);
    }

    public Team findTeamByName(String name) {
        return findByName(teams, name);
    }

    //  Match

    public void addMatch(MatchRecord record) {
        if (record != null) {
            matches.put(record.getId(), record);
        }
    }

    public boolean deleteMatch(int id) {
        return matches.remove(id) != null;
    }

    public MatchRecord findMatchById(int id) {
        return matches.get(id);
    }

    //  Admin

    public void addAdmin(Admin admin) {
        if (admin != null) {
            admins.put(admin.getId(), admin);
        }
    }

    public Admin findAdminById(int id) {
        return admins.get(id);
    }

    //  Bulk access

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public List<Hero> getAllHeroes() {
        return new ArrayList<>(heroes.values());
    }

    public List<Equipment> getAllEquipments() {
        return new ArrayList<>(equipments.values());
    }

    public List<Team> getAllTeams() {
        return new ArrayList<>(teams.values());
    }

    public List<MatchRecord> getAllMatches() {
        return new ArrayList<>(matches.values());
    }

    public void addMatchRecord(MatchRecord record) {
        addMatch(record);
    }

    public List<MatchRecord> getAllMatchRecords() {
        return getAllMatches();
    }

    public List<Player> getPlayersAsList() {
        return getAllPlayers();
    }

    public Player getPlayer(int id) {
        return findPlayerById(id);
    }

    public Hero getHero(int id) {
        return findHeroById(id);
    }

    public Equipment getEquipment(int id) {
        return findEquipmentById(id);
    }

    public Team getTeam(int id) {
        return findTeamById(id);
    }

    private static <T extends Searchable> T findByName(Map<Integer, T> map, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        for (T item : map.values()) {
            if (item.matches(keyword)) {
                return item;
            }
        }
        return null;
    }
}
