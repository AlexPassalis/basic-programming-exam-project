import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class TestBear extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp("src/data/week-2/t2-4a.txt");
        Location spawn_location = new Location(3, 5);
        testInitialization(new Bear(world, spawn_location), Bear.class);
    }
}
