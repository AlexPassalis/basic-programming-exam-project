import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.*;

public class Main {
    public static World world;

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

        Scanner scanner = new Scanner(new File(filepath));
        int size = scanner.nextInt(); // The size of the world defined in the input file.
        int display_size = 800;
        int delay = isTesting ? 15 : 300;

        HashMap<String, HashMap<String, Integer>> data = new HashMap<>(); // The HashMap containing the info from the input file.

        scanner.nextLine();
        int line_number = 1; // Track line number (starting from 1 after the size line)
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            line_number = line_number + 1;
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");

            String type = parts[0];
            if (!type.equals("grass") && !type.equals("rabbit") && !type.equals("burrow") && !type.equals("wolf") && !type.equals("bear")) {
                throw new IllegalArgumentException("Invalid type: " + type);
            }

            HashMap<String, Integer> values = new HashMap<>();
            String count_or_interval = parts[1];
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

        Program program = new Program(size, display_size, delay);
        world = program.getWorld();

        DisplayInformation GrassInfo = new DisplayInformation(Color.green, "custom-grass");
        program.setDisplayInformation(Grass.class, GrassInfo);
        DisplayInformation RabbitInfo = new DisplayInformation(Color.gray, "custom-rabbit");
        program.setDisplayInformation(Rabbit.class, RabbitInfo);
        DisplayInformation BurrowInfo = new DisplayInformation(Color.black, "custom-rabbit-hole");
        program.setDisplayInformation(Burrow.class, BurrowInfo);
        DisplayInformation WolfInfo = new DisplayInformation(Color.darkGray, "custom-wolf");
        program.setDisplayInformation(Wolf.class, WolfInfo);
        DisplayInformation BearInfo = new DisplayInformation(Color.orange, "custom-bear");
        program.setDisplayInformation(Bear.class, BearInfo);
        DisplayInformation BushInfo = new DisplayInformation(Color.green, "custom-bush-berries");
        program.setDisplayInformation(Bush.class, BushInfo);

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

            initialize(type, amount);
        }

        if (!isTesting) {
            program.show();
        }

        for (int i = 0; i < 200; i++) { // runs the program with 200 simulations
            program.simulate();
        }
    }

    public static World getWorld() {
        return world;
    }

    private static void initialize(String type, int amount) {
        Random rand = new Random();
        int size = world.getSize();

        // Create a single den for all wolves in this group
        Den wolfDen = null;
        Wolf alphaWolf = null;
        if (type.equals("wolf")) {
            wolfDen = new Den();
        }

        for (int i = 0; i < amount; i++) {
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            Location location = new Location(x, y);

            if (type.equals("grass") || type.equals("burrow")) {
                while (world.containsNonBlocking(location) || !world.isTileEmpty(location)) {
                    x = rand.nextInt(size);
                    y = rand.nextInt(size);
                    location = new Location(x, y);
                }
            } else {
                while (!world.isTileEmpty(location)) {
                    x = rand.nextInt(size);
                    y = rand.nextInt(size);
                    location = new Location(x, y);
                }
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
                    entity = new Rabbit(world);
                    break;
                case "wolf":
                    boolean isAlpha = i == 0;
                    Wolf wolf = new Wolf(world, wolfDen, isAlpha);

                    if (isAlpha) {
                        alphaWolf = wolf;
                    } else {
                        wolf.setAlpha(alphaWolf);
                        alphaWolf.addFollower(wolf);
                    }

                    entity = wolf;
                    break;
                case "bear":
                    entity = new Bear(world);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid entity type: " + type);
            }

            world.setTile(location, entity);
        }
    }

}