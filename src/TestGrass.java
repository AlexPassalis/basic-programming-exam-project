import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestGrass {
    private World world;

    public void setUp(String filename) throws FileNotFoundException {
        Main.main(new String[]{filename, "true"});
        world = Main.getWorld();
    }

    @AfterEach
    public void tearDown() {
        world = null;
    }

    @Test
    public void grass_gets_instantiated() throws FileNotFoundException {
        String filename = "t1-1a.txt";
        setUp(filename);

        Map<Object, Location> entities = world.getEntities();
        boolean world_contains_grass = false;

        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                world_contains_grass = true;
                break;
            }
        }

        assertTrue(world_contains_grass);
    }

    @Test
    public void grass_spreads() throws FileNotFoundException {
        String filename = "t1-1b.txt";
        setUp(filename);

        int grass_entities_in_file = 1;

        Map<Object, Location> entities_in_world = world.getEntities(); // Get all the entities in the world.
        int grass_entities_in_the_world = 0;
        for (Object entity : entities_in_world.keySet()) {
            if (entity instanceof Grass) {
                grass_entities_in_the_world = grass_entities_in_the_world + 1;
            } // If the entity is an instance of Grass, increase the count.
        }

        assertTrue(grass_entities_in_the_world > grass_entities_in_file);
    }

    @Test
    public void grass_is_nonblocking() throws FileNotFoundException {
        String filename = "t1-1c.txt";
        setUp(filename);

        Map<Object, Location> entities = world.getEntities();
        Location location = null;
        Grass grass = null;
         for(Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                grass = (Grass) entity; // grass has the methods from Grass now
                location = entities.get(entity);
                break;
            }
        }

        Rabbit testRabbit = new Rabbit();
        Location finalLocation = location;
        assertDoesNotThrow(() -> world.setTile(finalLocation, testRabbit));
    }
}

