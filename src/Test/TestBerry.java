package test;
import app.*;

import app.animal.Rabbit;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class TestBerry extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Berry());
    }

    @Test
    public void berries_regrow() throws Exception {
        setUp();
        Berry berry = new Berry();
        Location location = new Location (4,4);
        world.setTile(location, berry);
        assert(berry.getBerries() == 0); // Does not have any berries at spawn time

        for (int i = 0; i < 25; i++) {
            berry.act(world);
        }

        assert(berry.getBerries() == 1);
    }

    @Test
    public void berries_update_state_and_image() throws Exception {
        setUp();
        Berry berry = new Berry();
        Location location = new Location (4,4);
        world.setTile(location, berry);

        for (int i = 0; i < 25; i++) {
            berry.act(world);
        }
        Assertions.assertTrue(berry.getBerries() > 0); // Bush should have berries before being eaten

        DisplayInformation before = berry.getInformation();
        assertEquals("custom-bush-berries", before.getImageKey()); // Bush should show berries image before eating

        berry.getEaten(world);
        assert(berry.getBerries() == 0); // Bear should be able to eat berries

        DisplayInformation after = berry.getInformation();
        assertEquals("custom-bush", after.getImageKey()); // Bush should switch to empty bush image after being eaten

    }
}
