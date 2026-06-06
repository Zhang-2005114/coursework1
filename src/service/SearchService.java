import java.util.List;

public class SearchService {
    private final GameDataManager dataManager;

    public SearchService(GameDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Player findPlayerById(int id) {
        return dataManager.getPlayerById(id);
    }

    public Player findPlayerByName(String name) {
        List<Player> players = dataManager.getAllPlayers();
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public void displayPlayerDetails(Player p) {
        if (p == null) {
            System.out.println("Player not found.");
            return;
        }
        
        System.out.println("=== Player Details ===");
        System.out.println("ID: " + p.getId());
        System.out.println("Name: " + p.getName());
        System.out.println("Level: " + p.getLevel());
        System.out.println("Win Rate: " + String.format("%.2f%%", p.getWinRate() * 100));
        System.out.println("Match Count: " + p.getMatchCount());
        
        Team team = dataManager.getTeamById(p.getTeamId());
        if (team != null) {
            System.out.println("Team: " + team.getName() + " (ID: " + team.getId() + ")");
        } else {
            System.out.println("Team: None");
        }
        
        System.out.println("\nOwned Heroes:");
        List<Integer> ownedHeroIds = p.getOwnedHeroes();
        if (ownedHeroIds.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Integer heroId : ownedHeroIds) {
                Hero hero = dataManager.getHeroById(heroId);
                if (hero != null) {
                    System.out.println("  - " + hero.getName() + " (ID: " + hero.getId() + ")");
                }
            }
        }
        
        System.out.println("\nEquipped Items by Hero:");
        boolean hasEquipment = false;
        for (var entry : p.getEquippedItemsByHeroId().entrySet()) {
            int heroId = entry.getKey();
            List<Integer> equipmentIds = entry.getValue();
            Hero hero = dataManager.getHeroById(heroId);
            String heroName = hero != null ? hero.getName() : "Unknown Hero";
            
            System.out.println("  " + heroName + " (ID: " + heroId + "):");
            for (Integer equipId : equipmentIds) {
                Equipment equip = dataManager.getEquipmentById(equipId);
                if (equip != null) {
                    System.out.println("    - " + equip.getName() + " (ID: " + equipId + ")");
                    hasEquipment = true;
                }
            }
        }
        if (!hasEquipment) {
            System.out.println("  None");
        }
        System.out.println();
    }

    public Team findTeamById(int id) {
        return dataManager.getTeamById(id);
    }

    public Team findTeamByName(String name) {
        List<Team> teams = dataManager.getAllTeams();
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public void displayTeamOverview(Team t) {
        if (t == null) {
            System.out.println("Team not found.");
            return;
        }
        
        System.out.println("=== Team Overview ===");
        System.out.println("ID: " + t.getId());
        System.out.println("Name: " + t.getName());
        System.out.println("Average Level: " + String.format("%.2f", t.getAvgLevel()));
        System.out.println("Total Matches: " + t.getTotalMatches());
        System.out.println("Win Rate: " + String.format("%.2f%%", t.getWinRate() * 100));
        
        Player topPlayer = dataManager.getPlayerById(t.getTopPlayer());
        if (topPlayer != null) {
            System.out.println("Top Player: " + topPlayer.getName() + " (ID: " + topPlayer.getId() + ")");
        } else {
            System.out.println("Top Player: None");
        }
        
        System.out.println("\nTeam Members (" + t.getPlayerList().size() + "):");
        List<Integer> playerIds = t.getPlayerList();
        if (playerIds.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Integer playerId : playerIds) {
                Player player = dataManager.getPlayerById(playerId);
                if (player != null) {
                    System.out.println("  - " + player.getName() + " (ID: " + playerId + ", Level: " + player.getLevel() + ")");
                }
            }
        }
        System.out.println();
    }

    public Hero findHeroByName(String name) {
        List<Hero> heroes = dataManager.getAllHeroes();
        for (Hero hero : heroes) {
            if (hero.getName().equalsIgnoreCase(name)) {
                return hero;
            }
        }
        return null;
    }

    public void displayHeroDetails(Hero h) {
        if (h == null) {
            System.out.println("Hero not found.");
            return;
        }
        
        System.out.println("=== Hero Details ===");
        System.out.println("ID: " + h.getId());
        System.out.println("Name: " + h.getName());
        System.out.println("Type: " + (h.getType() != null ? h.getType().name() : "Unknown"));
        
        System.out.println("\nBase Stats:");
        if (h.getBaseStats().isEmpty()) {
            System.out.println("  None");
        } else {
            for (var entry : h.getBaseStats().entrySet()) {
                System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        System.out.println("\nCompatible Equipments:");
        List<Integer> equipIds = h.getCompatibleEquipments();
        if (equipIds.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Integer equipId : equipIds) {
                Equipment equip = dataManager.getEquipmentById(equipId);
                if (equip != null) {
                    System.out.println("  - " + equip.getName() + " (ID: " + equipId + ")");
                }
            }
        }
        
        System.out.println("\nOwner Players (" + h.getOwnerPlayers().size() + "):");
        List<Integer> ownerIds = h.getOwnerPlayers();
        if (ownerIds.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Integer ownerId : ownerIds) {
                Player player = dataManager.getPlayerById(ownerId);
                if (player != null) {
                    System.out.println("  - " + player.getName() + " (ID: " + ownerId + ")");
                }
            }
        }
        System.out.println();
    }
}