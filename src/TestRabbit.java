import itumulator.world.Location;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestRabbit extends TestSuper {
    @Test
    public void rabbit_gets_instantiated() throws FileNotFoundException {
        String filename = "t1-2a.txt";
        setUp(filename);
        int number_of_rabbits_in_file = 1; // 1 rabbit from the file

        // Retrieves all entities currently in the world map
        Map<Object, Location> entities = world.getEntities();
        int number_of_rabbits_in_world = 0;

        // Iterate through all entities to count how many are instances of Rabbit
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                number_of_rabbits_in_world = number_of_rabbits_in_world + 1;
            }
        }
        // Assert that the actual number of rabbits in the world matches the expected number
        assertEquals(number_of_rabbits_in_file, number_of_rabbits_in_world);
    }

    @Test
    public void rabbit_can_die() throws FileNotFoundException {
        String filename = "t1-2b.txt";
        setUp(filename);

        boolean hasRabbit = false;
        Map<Object, Location> entities = world.getEntities();

        // Search the world to see if any rabbits survived the simulation
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                hasRabbit = true;
                break; // If one rabbit is found, we stop searching
            }
        }
        // We assert false, because we expect 'hasRabbit' to be false
        assertFalse(hasRabbit);
    }
}
