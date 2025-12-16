package test;
import app.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import app.Carcass;

import java.io.FileNotFoundException;


public class TestFungi extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Fungi(20));
    }

    @Test
    public void carcass_with_fungi_spawns_fungi() throws FileNotFoundException {
        setUp("src/data/week-3/tf3-1a.txt");

        int number_of_carcasses = 0;
        for (Object entity : world.getEntities().keySet()) {
            if  (entity instanceof Carcass) {
                number_of_carcasses = number_of_carcasses + 1;
            }
        }
        assertTrue(number_of_carcasses >= 5 && number_of_carcasses <= 8);

        for (int i = 0; i < 10; i = i + 1) {
            program.simulate();
        }

        int number_of_fungi = 0;
        for (Object entity : world.getEntities().keySet()) {
            if (entity instanceof Fungi) {
                number_of_fungi = number_of_fungi + 1;
            }
        }
        assertEquals(number_of_carcasses, number_of_fungi);
    }
}


