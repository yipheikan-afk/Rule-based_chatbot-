import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Room Class
class Room {
    private String name;
    private String description;
    private Map<String, Room> exits = new HashMap<>();

    // Room Constructor
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Setting Exits for Rooms
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    // Getting a Room in a Specific Direction
    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    // Display Room Exits (Only for This Room)
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

    // Private Constructor (Singleton)
    private World() {
        createWorld();
    }

    // Singleton Access Method
    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    // World Creation Method
    private void createWorld() {
        // Creating Rooms
        Room forest = new Room("Forest", "A green forest, a greener forest on your left, a dark forest on your right.");
        Room greenForest = new Room("Green Forest", "You see a room with an enemy.");
        Room darkForest = new Room("Dark Forest", "A dark forest with a chest.");
        Room lockedRoom = new Room("Locked Room", "This room is locked.");
        Room chestRoom = new Room("Chest Room", "A chest in this room.");
        Room enemyRoom = new Room("Enemy Room", "A room with an enemy.");

        // Setting Exits
        forest.setExit("left", greenForest);
        forest.setExit("right", darkForest);

        // Green Forest Exits
        greenForest.setExit("forward", enemyRoom);
        greenForest.setExit("backward", forest);

        // Enemy Room Exits
        enemyRoom.setExit("forward", lockedRoom);
        enemyRoom.setExit("backward", greenForest);

        // Dark Forest Exits
        darkForest.setExit("forward", chestRoom);
        darkForest.setExit("backward", forest);

        // Chest Room (No Exits)
        chestRoom.setExit("backward", darkForest);

        // Storing Rooms in the World (No Extra Spaces)
        rooms.put("Forest", forest);
        rooms.put("Green Forest", greenForest);
        rooms.put("Dark Forest", darkForest);
        rooms.put("Locked Room", lockedRoom);
        rooms.put("Chest Room", chestRoom);
        rooms.put("Enemy Room", enemyRoom);
    }

    // Set Player to Forest Room and Display Only Forest Exits
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
}

// Main Class for Player Interaction
public class WorldDemo {
    public static void main(String[] args) {
        World world = World.getInstance();
        Room currentRoom = world.setForest();
        Scanner scanner = new Scanner(System.in);
        boolean weapon =false; 

        System.out.println("\nType 'left', 'right', 'forward', 'backward' to move. Type 'quit' to exit.");
        while (true) {
            System.out.print("Player: ");
            String command = scanner.nextLine().toLowerCase().trim();

            if (command.equals("quit")) {
                System.out.println("You have exited the game.");
                break;
            }

            // Moving Player
            Room nextRoom = currentRoom.getExit(command);
            if (nextRoom != null) {
                if (nextRoom.getName().equals("Enemy Room")){
                    System.out.println("an enemy is here. Do you want to fight or run?");
                    System.out.print("Type 'fight' or 'run': ");
                    String choice = scanner.nextLine();
                
                    if(choice.equals("fight")&&weapon==false){
                        System.out.println("you will need a weapon to fight");
                    }
                    else{
                        System.out.println(" you have deafeated the enemy");
                        // floor have key
                        //if pick up, the door will unlock
                    }
                    if(choice.equals("run")){
                        currentRoom = world.getRoom("Green Forest");
                    }
                }
            if (nextRoom.getName().equals("chestRoom")){



            }
                currentRoom = nextRoom;
                System.out.println("\nYou moved to: " + currentRoom.getName());
                System.out.println(currentRoom.getDescription());
                currentRoom.displayExits();
            } else {
                System.out.println("You can't move in that direction.");
                System.out.println("Available directions: ");
                currentRoom.displayExits();
            }
        }

        scanner.close();
    }
}
