import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;

import java.io.FileNotFoundException;

public class TestSuper {
    public World world;
    public boolean rabbit_can_die;

    public void setUp(String filename) throws FileNotFoundException {
        Main.main(new String[]{filename, "true"}); //We pass the filename to load the map
        world = Main.getWorld(); //Retrieves the World instance created by Main
    }

    @AfterEach
    void tearDown() {
        world = null; //Resets the world, to prevent data or state from test affecting next test
    }
}
