package model;


public class Admin extends Person {

    public Admin() {
        super();
        setRole("Admin");
    }

    public Admin(int id, String name, String password) {
        super(id, name, password, "Admin");
    }

    public void manageAll() {
        System.out.println("=== Admin Data Management ===");
        System.out.println("1. Add data");
        System.out.println("2. Delete data");
        System.out.println("3. Edit data");
        System.out.println("0. Back");
    }

    public void addData() {
        System.out.println("=== Add Data ===");
        System.out.println("Player, Hero, Equipment, Team, MatchRecord");
    }

    public void deleteData() {
        System.out.println("=== Delete Data ===");
        System.out.println("Player, Hero, Equipment, Team, MatchRecord");
    }

    public void editData() {
        System.out.println("=== Edit Data ===");
        System.out.println("Player, Hero, Equipment, Team, MatchRecord");
    }
}
