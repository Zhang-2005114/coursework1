import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameDataManager {
    private final Map<Integer, Player> players = new HashMap<>();
    private final Map<Integer, Hero> heroes = new HashMap<>();
    private final Map<Integer, Equipment> equipments = new HashMap<>();
    private final Map<Integer, Team> teams = new HashMap<>();
    private final List<MatchRecord> matchRecords = new ArrayList<>();
    private final Map<Integer, Admin> admins = new HashMap<>();

    public void initializeSampleData() {
        DataInitializer.loadSampleData(this);
    }

    // --- Player ---

    public void addPlayer(Player player) {
        if (player != null) {
            players.put(player.getId(), player);
        }
    }

    public boolean removePlayer(int id) {
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
        matchRecords.removeIf(record -> record.getPlayerId() == id);
        return true;
    }

    public boolean updatePlayer(Player player) {
        if (player == null || !players.containsKey(player.getId())) {
            return false;
        }
        players.put(player.getId(), player);
        return true;
    }

    public Player getPlayerById(int id) {
        return players.get(id);
    }

    // --- Hero ---

    public void addHero(Hero hero) {
        if (hero != null) {
            heroes.put(hero.getId(), hero);
        }
    }

    public boolean removeHero(int id) {
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
        for (MatchRecord record : matchRecords) {
            record.getPickedHeroes().remove(Integer.valueOf(id));
        }
        heroes.remove(id);
        return true;
    }

    public boolean updateHero(Hero hero) {
        if (hero == null || !heroes.containsKey(hero.getId())) {
            return false;
        }
        heroes.put(hero.getId(), hero);
        return true;
    }

    public Hero getHeroById(int id) {
        return heroes.get(id);
    }

    // --- Equipment ---

    public void addEquipment(Equipment equipment) {
        if (equipment != null) {
            equipments.put(equipment.getId(), equipment);
        }
    }

    public boolean removeEquipment(int id) {
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

    public boolean updateEquipment(Equipment equipment) {
        if (equipment == null || !equipments.containsKey(equipment.getId())) {
            return false;
        }
        equipments.put(equipment.getId(), equipment);
        return true;
    }

    public Equipment getEquipmentById(int id) {
        return equipments.get(id);
    }

    // --- Team ---

    public void addTeam(Team team) {
        if (team != null) {
            teams.put(team.getId(), team);
        }
    }

    public boolean removeTeam(int id) {
        Team removed = teams.remove(id);
        if (removed == null) {
            return false;
        }
        for (Player player : players.values()) {
            if (player.getTeamId() == id) {
                player.setTeamId(0);
            }
        }
        matchRecords.removeIf(record -> record.getTeamId() == id);
        return true;
    }

    public boolean updateTeam(Team team) {
        if (team == null || !teams.containsKey(team.getId())) {
            return false;
        }
        teams.put(team.getId(), team);
        return true;
    }

    public Team getTeamById(int id) {
        return teams.get(id);
    }

    // --- MatchRecord ---

    public void addMatchRecord(MatchRecord record) {
        if (record != null) {
            matchRecords.add(record);
        }
    }

    public boolean removeMatchRecord(int id) {
        Iterator<MatchRecord> iterator = matchRecords.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean updateMatchRecord(MatchRecord record) {
        if (record == null) {
            return false;
        }
        for (int i = 0; i < matchRecords.size(); i++) {
            if (matchRecords.get(i).getId() == record.getId()) {
                matchRecords.set(i, record);
                return true;
            }
        }
        return false;
    }

    public MatchRecord getMatchRecordById(int id) {
        for (MatchRecord record : matchRecords) {
            if (record.getId() == id) {
                return record;
            }
        }
        return null;
    }

    // --- Admin (login / sample data) ---

    public void addAdmin(Admin admin) {
        if (admin != null) {
            admins.put(admin.getId(), admin);
        }
    }

    public Admin getAdminById(int id) {
        return admins.get(id);
    }

    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins.values());
    }

    // --- Bulk access ---

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

    public List<MatchRecord> getAllMatchRecords() {
        return new ArrayList<>(matchRecords);
    }

    public List<Player> getPlayersAsList() {
        return getAllPlayers();
    }
}
