package Test;
import app.*;
import app.animal.Rabbit;
import app.animal.Wolf;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import app.Carcass;
import itumulator.world.*;
import java.io.FileNotFoundException;
import java.util.Map;


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


