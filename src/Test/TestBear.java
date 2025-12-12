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
        testInitialization(new Bear(false, spawn_location), Bear.class);
    }

    @Test
    public void bear_stays_in_territory() throws FileNotFoundException {
        setUp();
        Location spawn_location = new Location (3, 5);
        Bear bear = new Bear(false, spawn_location);
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
    @Test
    public void bear_hunts_in_territory() throws FileNotFoundException {
        setUp("src/data/empty-world-mini.txt");
        Location centre = new Location (1,1);
        Bear bear = new Bear(false, centre);
        world.setTile(centre, bear);
        Location rabbitLocation = new Location (0,0);
        Rabbit rabbit = new Rabbit(false);
        world.setTile(rabbitLocation, rabbit);

        final int MAX_STEPS = 30;
        boolean rabbitKilled = false;
        for (int step = 0; step < MAX_STEPS; step++) {
            bear.restoreEnergyForTesting();
            if (!world.contains(rabbit)) { rabbitKilled = true; break; }
        }
    }
}
