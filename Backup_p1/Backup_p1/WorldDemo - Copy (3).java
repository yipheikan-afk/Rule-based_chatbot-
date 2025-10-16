import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Room Class
class Room {
    private String name;
    private String description;
    private Map<String, Room> exits = new HashMap<>();

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void displayExits() {
        System.out.println("Exits:");
        for (String direction : exits.keySet()) {
            System.out.println(direction + " -> " + exits.get(direction).getName());
        }
        System.out.println();
    }
}

// World Class (Singleton)
class World {
    private static World instance;
    private Map<String, Room> rooms = new HashMap<>();

    private World() {
        createWorld();
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    private void createWorld() {
        Room forest = new Room("Forest", "A green forest, a greener forest on your left, a dark forest on your right.");
        Room greenForest = new Room("Green Forest", "You see a room with an enemy.");
        Room darkForest = new Room("Dark Forest", "A dark forest with a chest.");
        Room lockedRoom = new Room("Locked Room", "This room is locked.");
        Room chestRoom = new Room("Chest Room", "A chest in this room.");
        Room enemyRoom = new Room("Enemy Room", "A room with an enemy.");

        forest.setExit("left", greenForest);
        forest.setExit("right", darkForest);

        greenForest.setExit("forward", enemyRoom);
        greenForest.setExit("backward", forest);

        enemyRoom.setExit("forward", lockedRoom);
        enemyRoom.setExit("backward", greenForest);

        darkForest.setExit("forward", chestRoom);
        darkForest.setExit("backward", forest);

        chestRoom.setExit("backward", darkForest);

        rooms.put("Forest", forest);
        rooms.put("Green Forest", greenForest);
        rooms.put("Dark Forest", darkForest);
        rooms.put("Locked Room", lockedRoom);
        rooms.put("Chest Room", chestRoom);
        rooms.put("Enemy Room", enemyRoom);
    }

    public Room setForest() {
        Room forest = rooms.get("Forest");
        if (forest != null) {
            System.out.println("Player is now in: " + forest.getName());
            System.out.println(forest.getDescription());
            forest.displayExits();
            return forest;
        } else {
            System.out.println("Error: Forest room not found.");
            return null;
        }
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }
}

// Main Class
public class WorldDemo {
    public static boolean key = false;

    public static void main(String[] args) {
        World world = World.getInstance();
        Room currentRoom = world.setForest();
        Scanner scanner = new Scanner(System.in);
        boolean weapon = false;
        boolean room_unlocked = false;

        System.out.println("\nType 'left', 'right', 'forward', 'backward' to move. Type 'quit' to exit.");

        while (true) {
            System.out.print("Player: ");
            String command = scanner.nextLine().toLowerCase().trim();

            if (command.equals("quit")) {
                System.out.println("You have exited the game.");
                break;
            }

            Room nextRoom = currentRoom.getExit(command);
            if (nextRoom != null) {

                // Enemy Room logic
                if (nextRoom.getName().equals("Enemy Room")) {
                    System.out.println("An enemy is here. Do you want to fight or run?");
                    System.out.print("Type 'fight' or 'run': ");
                    String choice = scanner.nextLine().toLowerCase();

                    if (choice.equals("fight") && !weapon) {
                        System.out.println("You need a weapon to fight!");
                        continue;
                    } else if (choice.equals("fight") && weapon) {
                        System.out.println("You defeated the enemy!");
                        System.out.println("The dead enemy is holding a key. Do you want to pick it up? (yes/no)");
                        String choice2 = scanner.nextLine().toLowerCase();

                        if (choice2.equals("yes")) {
                            key = true;
                            room_unlocked = true;
                            System.out.println("You have picked up a key. A locked room is now unlocked.");

                            // ✅ Update the locked room description
                            Room lockedRoom = world.getRoom("Locked Room");
                            lockedRoom.setDescription("This room is unlocked.");
                        } else {
                            room_unlocked = false;
                        }
                    }

                    if (choice.equals("run")) {
                        currentRoom = world.getRoom("Green Forest");
                        System.out.println("You ran away to Green Forest.");
                        continue;
                    }
                }

                // Chest Room logic
                if (nextRoom.getName().equals("Chest Room")) {
                    System.out.println("You have opened the chest. You see a sword.");
                    System.out.println("Pick it up? (yes/no)");
                    String choice3 = scanner.nextLine().toLowerCase();

                    if (choice3.equals("yes")) {
                        weapon = true;
                        System.out.println("You have picked up a sword.");

                        currentRoom = world.getRoom("Dark Forest");
                        System.out.println("\nYou are now back in: " + currentRoom.getName());
                        System.out.println(currentRoom.getDescription());
                        currentRoom.displayExits();
                        continue;
                    }
                }

                // Locked Room check
                if (nextRoom.getName().equals("Locked Room") && !key) {
                    System.out.println("The room is locked. You need a key to enter.");
                    continue;
                }

                // Normal movement
                currentRoom = nextRoom;
                System.out.println("\nYou moved to: " + currentRoom.getName());
                System.out.println(currentRoom.getDescription());
                currentRoom.displayExits();

            } else {
                System.out.println("You can't move in that direction.");
                currentRoom.displayExits();
            }
        }

        scanner.close();
    }
}
