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
            System.out.println("Please provide input filename.");
            return;
        }
        String filename = args[0];
        boolean isTesting = false;

        if (args.length > 1) {
            isTesting = Boolean.parseBoolean(args[1]);
        }

        String folder_path = "src/data/week-1/";
        Scanner scanner = new Scanner(new File(folder_path + filename));
        int size = scanner.nextInt();
        int display_size = 800;
        int delay = 75;

        HashMap<String, HashMap<String, Integer>> data = new HashMap<>();

        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");

            String type = parts[0];
            if (!type.equals("grass") && !type.equals("rabbit") && !type.equals("burrow")) {
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
            data.put(type, values);
        }
        scanner.close();

        Program program = new Program(size, display_size, delay);
        world = program.getWorld();
        DisplayInformation GrassInfo = new DisplayInformation(Color.green, "GrassJava");
        program.setDisplayInformation(Grass.class, GrassInfo);

        for (Map.Entry<String, HashMap<String, Integer>> actor : data.entrySet()) {
            String type = actor.getKey();
            HashMap<String, Integer> count = actor.getValue();
            int amount = count.get("count");
            if (amount == 0) {
                int min = count.get("min");
                int max = count.get("max");
                amount = new Random().nextInt(max - min + 1) + min;
            }

            if (type.equals("grass")) {
                for (int i = 0; i < amount; i = i + 1) {
                    Random rand = new Random();
                    int x = rand.nextInt(size);
                    int y = rand.nextInt(size);

                    Location location = new Location(x, y);
                    while (!world.isTileEmpty(location)) {
                        x = rand.nextInt(size);
                        y = rand.nextInt(size);

                        location = new Location (x, y);
                    }
                    world.setTile(location, new Grass());
                }

                if (!isTesting) {
                    program.show();
                }

                for (int i = 0; i < 200; i++) {
                    program.simulate();
                }
            }
        }
    }

    public static World getWorld() {
        return world;
    }
}