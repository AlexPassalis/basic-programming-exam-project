import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class TestBear extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp("src/data/week-2/t2-4a.txt");
        testInitialization(new Bear(world), Bear.class);
    }
}
