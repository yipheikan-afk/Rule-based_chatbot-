import java.util.*;

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

// Player Class
class Player {
    private String name;
    private Room currentRoom;
    private List<String> inventory;

    public Player(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void addItem(String item) {
        if (!inventory.contains(item)) {
            inventory.add(item);
            System.out.println(item + " has been added to your inventory.");
        } else {
            System.out.println("You already have the " + item + ".");
        }
    }

    public boolean hasItem(String item) {
        return inventory.contains(item);
    }

    public void showInventory() {
        System.out.println(name + "'s Inventory:");
        if (inventory.isEmpty()) {
            System.out.println("- (empty)");
        } else {
            for (String item : inventory) {
                System.out.println("- " + item);
            }
        }
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
    public static void main(String[] args) {
        World world = World.getInstance();
        Scanner scanner = new Scanner(System.in);

        Player player1 = new Player("Player 1", world.setForest());
        Player player2 = new Player("Player 2", world.getRoom("Forest"));
        Player[] players = {player1, player2};

        System.out.println("\nType 'left', 'right', 'forward', 'backward' to move. Type 'inventory' to view items. Type 'quit' to exit.");

        int turn = 0;
        while (true) {
            Player currentPlayer = players[turn % 2];
            System.out.println("\n" + currentPlayer.getName() + "'s turn.");
            Room currentRoom = currentPlayer.getCurrentRoom();

            System.out.println("You are in: " + currentRoom.getName());
            System.out.println(currentRoom.getDescription());
            currentRoom.displayExits();

            System.out.print("Command: ");
            String command = scanner.nextLine().toLowerCase().trim();

            if (command.equals("quit")) {
                System.out.println(currentPlayer.getName() + " has exited the game.");
                break;
            }

            if (command.equals("inventory")) {
                currentPlayer.showInventory();
                turn++;
                continue;
            }

            Room nextRoom = currentRoom.getExit(command);
            if (nextRoom != null) {

                if (nextRoom.getName().equals("Enemy Room")) {
                    System.out.println("An enemy is here. Do you want to fight or run?");
                    System.out.print("Type 'fight' or 'run': ");
                    String choice = scanner.nextLine().toLowerCase();

                    if (choice.equals("fight") && !currentPlayer.hasItem("Sword")) {
                        System.out.println("You need a sword to fight!");
                        turn++;
                        continue;
                    } else if (choice.equals("fight")) {
                        System.out.println("You defeated the enemy!");
                        System.out.println("The enemy dropped a key. Pick it up? (yes/no): ");
                        String pickKey = scanner.nextLine().toLowerCase();
                        if (pickKey.equals("yes")) {
                            currentPlayer.addItem("Key");
                            Room lockedRoom = world.getRoom("Locked Room");
                            lockedRoom.setDescription("This room is unlocked.");
                        }
                    } else if (choice.equals("run")) {
                        currentPlayer.setCurrentRoom(world.getRoom("Green Forest"));
                        System.out.println("You ran away to Green Forest.");
                        turn++;
                        continue;
                    }
                }

                if (nextRoom.getName().equals("Chest Room")) {
                    System.out.println("You found a chest. Open it? (yes/no): ");
                    String openChest = scanner.nextLine().toLowerCase();
                    if (openChest.equals("yes")) {
                        System.out.println("You found a sword inside.");
                        currentPlayer.addItem("Sword");
                        currentPlayer.setCurrentRoom(world.getRoom("Dark Forest"));
                        turn++;
                        continue;
                    }
                }

                if (nextRoom.getName().equals("Locked Room") && !currentPlayer.hasItem("Key")) {
                    System.out.println("The room is locked. You need a key.");
                    turn++;
                    continue;
                }

                currentPlayer.setCurrentRoom(nextRoom);
                System.out.println("\nYou moved to: " + nextRoom.getName());
                System.out.println(nextRoom.getDescription());
                nextRoom.displayExits();
            } else {
                System.out.println("You can't move in that direction.");
                currentRoom.displayExits();
            }
            turn++;
        }
        scanner.close();
    }
}
