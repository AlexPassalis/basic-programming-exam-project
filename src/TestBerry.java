import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBerry extends TestSuper {
    @Test
    public void berries_regrow() throws Exception {
        setUp();
        Program program = Main.getProgram();
        World world = Main.getWorld();
        Berry berry = new Berry(program);
        Location location = new Location (4,4);
        world.setTile(location, berry);
        assertFalse(berry.hasBerries()); // Does not have any berries at spawn time

        for (int i = 0; i < 25; i++) {
            berry.act(world);
        }

        assertTrue(berry.hasBerries());
    }

    @Test
    public void berries_update_state_and_image() throws Exception {
        setUp();
        Program program = Main.getProgram();
        World world = Main.getWorld();
        Berry berry = new Berry(program);
        Location location = new Location (4,4);
        world.setTile(location, berry);

        for (int i = 0; i < 25; i++) {
            berry.act(world);
        }
        assertTrue(berry.hasBerries()); //Bush should have berries before being eaten

        DisplayInformation before = berry.getInformation();
        assertEquals("custom-bush-berries", before.getImageKey()); // Bush should show berries image before eating

        int eaten = berry.eatBerries();
        assertEquals(1, eaten); // Bear should be able to eat berries

        DisplayInformation after = berry.getInformation();
        assertEquals("custom-bush", after.getImageKey()); // Bush should switch to empty bush image after being eaten

    }
}
