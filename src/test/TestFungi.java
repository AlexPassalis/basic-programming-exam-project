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
        testInitialization(new Fungi(10));
    }

    @Test
    public void carcass_with_fungi_spawns_fungi() throws FileNotFoundException {
        setUp("src/data/week-3/tf3-1a.txt");

        boolean carcassesExist = true;
        while (carcassesExist) {
            carcassesExist = false;
            for (Object object:world.getEntities().keySet()) {
                if (object instanceof Carcass) {
                    ((Carcass) object).act(world);
                    carcassesExist = true;
                }
            }
        }
        boolean fungiExists = false;
        for (Object object:world.getEntities().keySet()) {
            if (object instanceof Fungi) {
                fungiExists = true;
                break;
            }
        }
        assertTrue(fungiExists);
    }
}


