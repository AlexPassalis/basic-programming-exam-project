import itumulator.world.Location;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestBurrow extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Burrow(), Burrow.class);
    }
}
