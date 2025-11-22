import itumulator.world.Location;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGrass extends TestSuper {
    @Test
    public void grass_gets_instantiated() throws FileNotFoundException {
        String filename = "t1-1a.txt";  // defines the specific input file for the test
        setUp(filename); // Initialize the world and run the simulation

        // Retrieves a map of all actors and locations currently in the world
        Map<Object, Location> entities = world.getEntities();
        boolean world_contains_grass = false;

        // Iterate through every entity in the world to check type
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                world_contains_grass = true;
                break; // We found grass, so we can stop searching
            }
        }
        //Assert true if we found at least one Grass object, fails otherwise
        assertTrue(world_contains_grass);
    }

    @Test
    public void grass_spreads() throws FileNotFoundException {
        String filename = "t1-1b.txt";
        setUp(filename);

        //  1 grass block from the input file
        int grass_entities_in_file = 1;

        Map<Object, Location> entities_in_world = world.getEntities(); // Get all the entities in the world.
        int grass_entities_in_the_world = 0;
        for (Object entity : entities_in_world.keySet()) {
            if (entity instanceof Grass) {
                grass_entities_in_the_world = grass_entities_in_the_world + 1;
            } // If the entity is an instance of Grass, increase the count.
        }
         // Verify that the amount of grass is now greater than 1
        assertTrue(grass_entities_in_the_world > grass_entities_in_file);
    }

    @Test
    public void grass_is_nonblocking() throws FileNotFoundException {
        String filename = "t1-1c.txt";
        setUp(filename);

        Map<Object, Location> entities = world.getEntities();
        Location location = null;

        // Search for a Rabbit to find out where it is standing
         for(Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {

                // Extracts coordinates of the Rabbit
                int x = entities.get(entity).getX();
                int y = entities.get(entity).getY();
                location = new Location(x, y);

                break; //Stop after finding the first rabbit
            }
        }
        // Asks the world if this specific location contains a nonblocking object
        boolean location_contains_non_blocking = world.containsNonBlocking(location);

         // Assert that there is grass under the rabbit
        assertTrue(location_contains_non_blocking);
    }
}

