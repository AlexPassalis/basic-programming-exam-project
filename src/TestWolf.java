import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class TestWolf extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Wolf(world), Wolf.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        testDeath(new Wolf(world), Wolf.class);
    }
}
