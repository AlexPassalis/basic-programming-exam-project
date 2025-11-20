import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.simulator.Actor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {
        int display_size = 800;
        int delay = 75;

        File folder = new File("./src/data/week-1");
        File[] files = folder.listFiles();

        if (files == null) {
            System.err.println("Folder not found or not a directory");
            return;
        }

        for (File file : files) {
                Scanner sc = new Scanner(file);
                
                int size = sc.nextInt();
                HashMap<String, HashMap<String, Integer>> data = new HashMap<>();    
                
                sc.nextLine();
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
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

            Program p = new Program(size, display_size, delay);
            World w = p.getWorld();
            DisplayInformation GrassInfo = new DisplayInformation(Color.green, "GrassJava");
            p.setDisplayInformation(Grass.class, GrassInfo);
            p.show();

            for (Map.Entry<String, HashMap<String, Integer>> entry : data.entrySet()) {
                String type = entry.getKey();
                if (type.equals("grass")) {

                }

                if (type.equals("rabbit")) {

                }

                if (type.equals("burrow")) {

                }
            }
        }
            // w.setTile(new Location(0, 0), new <MyClass>());

          // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

          // p.show();
    }
}