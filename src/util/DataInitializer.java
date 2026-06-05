import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class DataInitializer {
    private DataInitializer() {
    }

    public static GameDataManager createSampleData() {
        GameDataManager manager = new GameDataManager();
        loadSampleData(manager);
        return manager;
    }

    public static void loadSampleData(GameDataManager manager) {
        if (manager == null) {
            return;
        }

        createEquipments(manager);
        createHeroes(manager);
        createPlayers(manager);
        createTeams(manager);
        createAdmins(manager);
        createMatchRecords(manager);
        linkHeroOwners(manager);
        refreshTeamStats(manager);
    }

    private static void createEquipments(GameDataManager manager) {
        EquipmentType[] types = {
                EquipmentType.ATTACK, EquipmentType.DEFENSE, EquipmentType.SPELL
        };
        String[] names = {
                "Storm Blade", "Guard Plate", "Arcane Staff", "Fury Bow", "Iron Helm",
                "Shadow Dagger", "Crystal Shield", "Flame Ring", "Wind Boots", "Life Gem",
                "Thunder Hammer", "Frost Armor", "Spirit Cloak", "Blood Sword", "Holy Relic",
                "Venom Fang", "Dragon Scale", "Moon Orb", "Sun Pendant", "Void Lens"
        };
        for (int i = 0; i < 20; i++) {
            Equipment equipment = new Equipment(
                    i + 1,
                    names[i],
                    types[i % types.length],
                    10 + i,
                    0.45 + (i % 5) * 0.03);
            manager.addEquipment(equipment);
        }
    }

    private static void createHeroes(GameDataManager manager) {
        HeroType[] types = {
                HeroType.MAGE, HeroType.SHOOTER, HeroType.TANK,
                HeroType.JUNGLER, HeroType.SUPPORT
        };
        String[] names = {
                "Li Bai", "Han Xin", "Liu Bang", "Xiang Yu", "Sun Shangxiang",
                "Zhao Yun", "Diao Chan", "Angela", "Da Qiao", "Xiao Qiao",
                "Arthur", "Hou Yi", "Ying Zheng", "DongHuang Taiyi", "Mozi",
        };
        for (int i = 0; i < 15; i++) {
            Hero hero = new Hero(i + 1, names[i], types[i % types.length]);
            hero.setStat("attack", 80 + i * 3);
            hero.setStat("defense", 50 + i * 2);
            hero.setStat("hp", 3000 + i * 100);
            int eq1 = (i * 2) % 20 + 1;
            int eq2 = (i * 2 + 1) % 20 + 1;
            hero.addCompatibleEquipment(eq1);
            hero.addCompatibleEquipment(eq2);
            manager.addHero(hero);
            linkEquipmentToHero(manager, hero.getId(), eq1);
            linkEquipmentToHero(manager, hero.getId(), eq2);
        }
    }

    private static void createPlayers(GameDataManager manager) {
        String[] names = {
                "Tom", "Jack", "Mike", "David", "Peter",
                "John", "Paul", "Mark", "James", "Robert",
                "William", "Richard", "Joseph", "Daniel", "Henry"
        };
        for (int i = 0; i < 15; i++) {
            int teamId = i / 5 + 1;
            Player player = new Player(
                    i + 1,
                    names[i],
                    "player" + (i + 1),
                    10 + i % 8,
                    0.45 + (i % 6) * 0.05,
                    teamId,
                    12 + i);
            for (int h = 0; h < 3; h++) {
                int heroId = (i * 3 + h) % 15 + 1;
                player.addOwnedHero(heroId);
                Hero hero = manager.findHeroById(heroId);
                if (hero != null) {
                    List<Integer> equipped = new ArrayList<>(hero.getCompatibleEquipments());
                    if (!equipped.isEmpty()) {
                        player.setEquippedItemsForHero(heroId, List.of(equipped.get(0)));
                    }
                }
            }
            manager.addPlayer(player);
        }
    }

    private static void createTeams(GameDataManager manager) {
        String[] teamNames = {"Team1", "Team2", "Team3"};
        for (int t = 1; t <= 3; t++) {
            Team team = new Team(t, teamNames[t - 1]);
            for (int p = (t - 1) * 5 + 1; p <= t * 5; p++) {
                team.addPlayer(p);
            }
            manager.addTeam(team);
        }
    }

    private static void createAdmins(GameDataManager manager) {
        Admin admin = new Admin(1, "admin", "admin123");
        manager.addAdmin(admin);
    }

    private static void createMatchRecords(GameDataManager manager) {
        String[] opponents = {
                "Shadow Legion", "Iron Wolves", "Crystal Empire", "Storm Riders", "Night Clan",
                "Solar Guards", "Frost Giants", "Flame Order", "Thunder House", "Silent Blade"
        };
        MatchResult[] results = {MatchResult.WIN, MatchResult.LOSE, MatchResult.WIN,
                MatchResult.WIN, MatchResult.LOSE, MatchResult.WIN, MatchResult.LOSE,
                MatchResult.WIN, MatchResult.WIN, MatchResult.LOSE};
        LocalDate baseDate = LocalDate.of(2026, 3, 1);

        for (int i = 0; i < 10; i++) {
            int playerId = (i % 15) + 1;
            Player player = manager.findPlayerById(playerId);
            int teamId = player != null ? player.getTeamId() : 1;
            List<Integer> picks = new ArrayList<>();
            if (player != null && !player.getOwnedHeroes().isEmpty()) {
                picks.add(player.getOwnedHeroes().get(0));
                if (player.getOwnedHeroes().size() > 1) {
                    picks.add(player.getOwnedHeroes().get(1));
                }
            } else {
                picks.add(1);
            }
            MatchRecord record = new MatchRecord(
                    i + 1,
                    baseDate.plusDays(i * 2L),
                    playerId,
                    teamId,
                    opponents[i],
                    results[i],
                    picks);
            manager.addMatchRecord(record);
        }
    }

    private static void linkHeroOwners(GameDataManager manager) {
        for (Player player : manager.getAllPlayers()) {
            for (Integer heroId : player.getOwnedHeroes()) {
                Hero hero = manager.findHeroById(heroId);
                if (hero != null) {
                    hero.addOwnerPlayer(player.getId());
                }
            }
        }
    }

    private static void linkEquipmentToHero(GameDataManager manager, int heroId, int equipmentId) {
        Equipment equipment = manager.findEquipmentById(equipmentId);
        if (equipment != null) {
            equipment.addCompatibleHero(heroId);
        }
    }

    private static void refreshTeamStats(GameDataManager manager) {
        List<Player> players = manager.getPlayersAsList();
        List<MatchRecord> matches = manager.getAllMatchRecords();
        for (Team team : manager.getAllTeams()) {
            team.recalculateStats(players, matches);
        }
    }
}
