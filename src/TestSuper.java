import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;

import java.io.FileNotFoundException;

public class TestSuper {
    public World world;
    public boolean rabbit_can_die;

    public void setUp(String filename) throws FileNotFoundException {
        Main.main(new String[]{filename, "true"});
        world = Main.getWorld();
    }

    @AfterEach
    void tearDown() {
        world = null; //Resets the world, to prevent data or state from test
    }
}
