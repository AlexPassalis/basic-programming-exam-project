package test;
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
    public void carcass_with_fungi_spawns_fungi () throws FileNotFoundException {
        setUp("src/data/week-3/tf3-1a.txt");

        Location carcassLocation = null;
        Carcass carcass = null;

        int size = world.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Location location = new Location (x,y);
                Object nonBlocking = world.getNonBlocking(location);

                if (nonBlocking instanceof Carcass) {
                    carcassLocation = location;
                    carcass = (Carcass) nonBlocking;
                }
            }
        }
        assertTrue(carcass != null);

        while (world.getNonBlocking(carcassLocation) instanceof Carcass) {
            carcass.act(world);
        }
        Object result = world.getNonBlocking(carcassLocation);
        assertTrue(!(result instanceof Carcass));
        assertTrue(result instanceof Fungi);
    }
}


