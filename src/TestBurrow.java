import itumulator.world.Location;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestBurrow extends TestSuper {
    @Test
    public void burrow_gets_instantiated() throws FileNotFoundException {
        String filename = "t1-3a.txt";
        setUp(filename);
        int number_of_burrows_in_file = 1;

        Map<Object, Location> entities = world.getEntities();
        int number_of_burrows_in_world = 0;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Burrow) {
                number_of_burrows_in_world = number_of_burrows_in_world + 1;
            }
        }

        assertEquals(number_of_burrows_in_file, number_of_burrows_in_world);
    }
}
