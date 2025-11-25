import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class TestWolf extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        testInitialization(new Wolf(world, den), Wolf.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        testDeath(new Wolf(world, den), Wolf.class);
    }
}
