import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestMain {
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
    public void grass() throws FileNotFoundException {
        String filename = "t1-1a.txt";
        setUp(filename);

        int number_of_grass_entities_in_file = 3;

        Map<Object, Location> entities_in_world = world.getEntities();
        int number_of_grass_entities_in_the_world = 0;
        for (Object entity : entities_in_world.keySet()) {
            if (entity instanceof Grass) {
                number_of_grass_entities_in_the_world = number_of_grass_entities_in_the_world + 1;
            }
        }

        assertEquals(number_of_grass_entities_in_file, number_of_grass_entities_in_the_world);
    }
}
