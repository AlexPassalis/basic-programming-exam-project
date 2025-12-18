package app;

import java.awt.Color;

import app.animal.Bear;
import app.animal.Deer;
import app.animal.Rabbit;
import app.animal.Wolf;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.*;


/* TODO
- refactor big code into simpler functions e.g. in Main extractDataFromFile
- Use java doc for functions descriptions
- Use abstract class in Animal for the functions that do not have a body
- Use interfaces for e.g. eatable, for the rabbit and grass ...
- Split the code into modules, e.g. Animals and Nonblocking elements
*/

public class Main {
    private static Program program;
    private static World world;

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println("Please provide input file path.");
            return;
        } // If the user does not provide the file path that Main will generate the world out of, notify him.
        String filepath = args[0];

        boolean isTesting = false;
        if (args.length > 1) {
            isTesting = Boolean.parseBoolean(args[1]);
        } // Use this boolean for test specific configuration.

        ParseInputFileReturnType input_file_info = parseInputFile(filepath);
        configureWorld(input_file_info);

        if (!isTesting) {
            int simulation_counts = 200;
            runProgram(simulation_counts, filepath);
        }
    }

    private static class ParseInputFileReturnType {
        int size;
        HashMap<String, HashMap<String, Integer>> data;

        ParseInputFileReturnType(int size, HashMap<String, HashMap<String, Integer>> data) {
            this.size = size;
            this.data = data;
        }
    }

    private static ParseInputFileReturnType parseInputFile(String filepath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filepath));
        int size = scanner.nextInt(); // The size of the world defined in the input file.

        HashMap<String, HashMap<String, Integer>> data = new HashMap<>(); // The HashMap containing the info from the input file.

        scanner.nextLine();
        int line_number = 0; // Track line number (0 indexed)
        while (scanner.hasNextLine()) {
            line_number = line_number + 1;

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String type = parts[0].trim().toLowerCase(); // Remove unnecessary spaces and convert to lowercase
            if (!type.equals("grass") && !type.equals("rabbit") && !type.equals("burrow") && !type.equals("wolf") && !type.equals("bear") && !type.equals("berry") && !type.equals("carcass") && !type.equals("deer")) {
                throw new IllegalArgumentException("Invalid type: " + type);
            }

            HashMap<String, Integer> values = new HashMap<>();

            String count_or_interval = parts[1];
            if (parts.length > 2) {
                String spawn_location = null;
                if (parts.length >= 3 && parts[1].equals("fungi")) {
                    count_or_interval = parts[2];
                    values.put("fungi", 1);
                    if (parts.length == 4) {
                        spawn_location = parts[3];
                    }
                } else {
                    spawn_location = parts[2];
                }

                if (spawn_location != null) {
                    String coordinates = spawn_location.replace("(", "").replace(")", "");
                    String[] coords = coordinates.split(",");
                    int spawn_x = Integer.parseInt(coords[0]);
                    int spawn_y = Integer.parseInt(coords[1]);
                    values.put("spawn_x", spawn_x);
                    values.put("spawn_y", spawn_y);
                }
            }

            if (!count_or_interval.contains("-")) {
                int count = Integer.parseInt(count_or_interval);
                values.put("count", count);
                values.put("min", 0);
                values.put("max", 0);
            } else {
                String[] range = count_or_interval.split("-");
                values.put("count", 0);
                values.put("min", Integer.parseInt(range[0]));
                values.put("max", Integer.parseInt(range[1]));
            }

            // Create unique key using type and line number
            String unique_key = type + "_" + line_number;
            data.put(unique_key, values);
        }

        scanner.close();

        ParseInputFileReturnType result = new ParseInputFileReturnType(size, data);
        return result;
    }

    private static void configureWorld(ParseInputFileReturnType input_file_info) {
        int size = input_file_info.size;
        int display_size = 800;
        int delay = 750;

        program = new Program(size, display_size, delay);
        world = program.getWorld();

        configureDisplayInformation();

        HashMap<String, HashMap<String, Integer>> data = input_file_info.data;
        initializeAllObjects(data);
    }

    private static void configureDisplayInformation() {
        DisplayInformation GrassInfo = new DisplayInformation(Color.green, "custom-grass");
        program.setDisplayInformation(Grass.class, GrassInfo);
        DisplayInformation RabbitInfo = new DisplayInformation(Color.gray, "custom-rabbit");
        program.setDisplayInformation(Rabbit.class, RabbitInfo);
        DisplayInformation BurrowInfo = new DisplayInformation(Color.black, "custom-rabbit-hole");
        program.setDisplayInformation(Burrow.class, BurrowInfo);
        DisplayInformation WolfInfo = new DisplayInformation(Color.darkGray, "custom-wolf");
        program.setDisplayInformation(Wolf.class, WolfInfo);
        DisplayInformation DenInfo = new DisplayInformation(Color.red, "custom-den");
        program.setDisplayInformation(Den.class, DenInfo);
        DisplayInformation BearInfo = new DisplayInformation(Color.orange, "custom-bear");
        program.setDisplayInformation(Bear.class, BearInfo);
        DisplayInformation BerryInfo = new DisplayInformation(Color.green, "custom-bush");
        program.setDisplayInformation(Berry.class, BerryInfo);
        DisplayInformation CarcassInfo = new DisplayInformation(Color.magenta, "carcass");
        program.setDisplayInformation(Carcass.class, CarcassInfo);
        DisplayInformation FungiInfo = new DisplayInformation(Color.green, "fungi");
        program.setDisplayInformation(Fungi.class, FungiInfo);
        DisplayInformation DeerInfo = new DisplayInformation(Color.LIGHT_GRAY, "deer");
        program.setDisplayInformation(Deer.class, DeerInfo);
    }

    private static void initializeAllObjects(HashMap<String, HashMap<String, Integer>> data) {
        for (Map.Entry<String, HashMap<String, Integer>> actor : data.entrySet()) {
            String unique_key = actor.getKey();
            String type = unique_key.split("_")[0]; // Extract "wolf" from "wolf_2"
            HashMap<String, Integer> count = actor.getValue();
            int amount = count.get("count");
            if (amount == 0) {
                int min = count.get("min");
                int max = count.get("max");
                amount = new Random().nextInt(max - min + 1) + min;
            }

            Location spawn_location = null;
            Integer spawn_x = count.get("spawn_x");
            if (spawn_x != null) {
                Integer spawn_y = count.get("spawn_y");
                spawn_location = new Location(spawn_x, spawn_y);
            }

            boolean carcass_has_fungi = count.getOrDefault("fungi", 0) == 1; // default to 0 since, it might be null

            initializeSingleObject(type, amount, spawn_location, carcass_has_fungi);
        }
    }

    private static void initializeSingleObject(String type, int amount, Location spawn_location, boolean carcass_has_fungi) {
        Den wolf_den = null; // Create a single den for all wolves in this group
        Wolf alpha_wolf = null;

        for (int i = 0; i < amount; i = i + 1) {
            Location location;
            if (spawn_location != null) {
                location = spawn_location;
                while (!world.isTileEmpty(location)) {
                    Set<Location> surrounding = world.getSurroundingTiles(spawn_location);
                    if (!surrounding.isEmpty()) {
                        location = surrounding.iterator().next();
                    } else {
                        location = getEmptyLocation();
                        break;
                    }
                }
            } else {
                location = type.equals("grass") || type.equals("burrow") || type.equals("berry") ? getEmptyNonBlockingLocation() : getEmptyLocation();
            }

            Object entity;
            switch (type) {
                case "grass":
                    entity = new Grass();
                    break;
                case "burrow":
                    entity = new Burrow();
                    break;
                case "rabbit":
                    entity = new Rabbit(world, carcass_has_fungi);
                    break;
                case "wolf":
                    boolean isAlpha = i == 0;
                    if (isAlpha) {
                        Location den_location = getEmptyNonBlockingLocation();
                        wolf_den = new Den();
                        world.setTile(den_location, wolf_den);

                        alpha_wolf = new Wolf(world, carcass_has_fungi, wolf_den);
                        entity = alpha_wolf;
                    } else {
                        Wolf nonAlfaWolf = new Wolf(world, carcass_has_fungi, wolf_den, alpha_wolf);
                        entity = nonAlfaWolf;
                    }
                    break;
                case "bear":
                    entity = new Bear(world, carcass_has_fungi, location);
                    break;
                case "berry":
                    entity = new Berry();
                    break;
                case "carcass":
                    entity = new Carcass(carcass_has_fungi);
                    break;
                case "deer":
                    entity = new Deer(world, carcass_has_fungi);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid entity type: " + type);
            }

            world.setTile(location, entity);
        }
    }

    private static Location getEmptyLocation() {
        Random randon_number = new Random();
        int size = world.getSize();

        int x = randon_number.nextInt(size);
        int y = randon_number.nextInt(size);
        Location location = new Location(x, y);

        while (!world.isTileEmpty(location)) {
            x = randon_number.nextInt(size);
            y = randon_number.nextInt(size);
            location = new Location(x, y);
        }

        return location;
    }

    private static Location getEmptyNonBlockingLocation() {
        Random randon_number = new Random();
        int size = world.getSize();

        int x = randon_number.nextInt(size);
        int y = randon_number.nextInt(size);
        Location location = new Location(x, y);

        while (world.containsNonBlocking(location)) {
            x = randon_number.nextInt(size);
            y = randon_number.nextInt(size);
            location = new Location(x, y);
        }

        return location;
    }

    private static void runProgram(int simulation_counts, String filepath) {
        program.show();

        ArrayList<String> problematic_filepaths = new ArrayList<>(List.of("src/data/week-2/t2-3a.txt", "src/data/week-2/t2-4b.txt", "src/data/week-2/t2-5a.txt"));

        for (int i = 0; i < simulation_counts; i++) {
            program.simulate();

            if (problematic_filepaths.contains(filepath)) {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public static World getWorld() {
        return world;
    }

    public static Program getProgram() {
        return program;
    }
}