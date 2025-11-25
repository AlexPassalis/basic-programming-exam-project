import itumulator.world.Location;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGrass extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Grass(), Grass.class);
    }

    @Test
    public void spreads() throws FileNotFoundException {
        setUp("src/data/week-1/t1-1b.txt");

        int grass_entities_in_file = 1; // The input file contains 1 grass

        Map<Object, Location> entities_in_world = world.getEntities(); // Get all the entities in the world.
        int grass_entities_in_the_world = 0;
        for (Object entity : entities_in_world.keySet()) {
            if (entity instanceof Grass) {
                grass_entities_in_the_world = grass_entities_in_the_world + 1;
            } // If the entity is an instance of Grass, increase the count.
        }

        assertTrue(grass_entities_in_the_world > grass_entities_in_file); // Assert that there are more grass instances than when we started
    }

    @Test
    public void is_nonblocking() throws FileNotFoundException {
        setUp();

        Location center = new Location(5, 5); // Create a location in the center of the world (world is 10x10 from empty-world.txt)

        // First, place grass at the center
        Grass grass = new Grass();
        world.setTile(center, grass);

        // Then, place a rabbit on top of the grass at the same location
        Rabbit rabbit = new Rabbit(world);
        world.setTile(center, rabbit);

        // Verify that the location contains a non-blocking object (grass underneath)
        boolean location_contains_non_blocking = world.containsNonBlocking(center);

        // Assert that there is grass under the rabbit (grass is non-blocking)
        assertTrue(location_contains_non_blocking);
    }
}

