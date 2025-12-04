package test;

import app.*;

import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class TestBear extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        Location spawn_location = new Location(3, 5);
        testInitialization(new Bear(spawn_location), Bear.class);
    }

    @Test
    public void bear_stays_in_territory() throws FileNotFoundException {
        setUp();
        Location spawn_location = new Location (3, 5);
        Bear bear = new Bear(spawn_location);
        world.setTile(spawn_location, bear);

        for (int i = 0; i < 5; i = i + 1) {
            for (int j = 0; j < 5; j = j + 1) {
                bear.act(world);
                bear.restoreEnergyForTesting(); // Calls method from bear, that restores 100 energy
            }

            Location current = world.getLocation(bear);
            int distance = Math.abs(current.getX() - spawn_location.getX()) + Math.abs(current.getY() - spawn_location.getY());
            int territoryRadius = 3;
            assert distance <= territoryRadius;
        }
    }
}
