package test;

import app.animal.Bear;
import app.animal.Rabbit;
import app.Berry;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBear extends TestSuper {
    @Test
    public void gets_initialized() throws FileNotFoundException {
        getsInitialized("src/data/week-2/t2-4b.txt", Bear.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        animalDies(new Bear(world, false, new Location(0, 0)));
    }

    @Test
    public void bear_stays_in_territory() throws FileNotFoundException {
        setUp();
        Location spawn_location = new Location (3, 5);
        Bear bear = new Bear(world, false, spawn_location);
        world.setTile(spawn_location, bear);

        for (int i = 0; i < 25; i++) {
            bear.act(world);
            bear.restoreEnergyForTesting();

            Location current = world.getLocation(bear);
            int distance = Math.abs(current.getX() - spawn_location.getX())
                    + Math.abs(current.getY() - spawn_location.getY());
            assert distance <= 3;
        }
    }

    @Test
    public void bear_hunts_in_territory() throws FileNotFoundException {
        setUp("src/data/empty-world-mini.txt");
        Location centre = new Location (1,1);
        Bear bear = new Bear(world, false, centre);
        world.setTile(centre, bear);
        Location rabbitLocation = new Location (0,0);
        Rabbit rabbit = new Rabbit(world, false);
        world.setTile(rabbitLocation, rabbit);

        final int MAX_STEPS = 30;
        boolean rabbitKilled = false;
        for (int step = 0; step < MAX_STEPS; step++) {
            bear.restoreEnergyForTesting();
            if (!world.contains(rabbit)) { rabbitKilled = true; break; }
            bear.act(world);
            if (!world.contains(rabbit)) { rabbitKilled = true; break; }
        }
        assertTrue(rabbitKilled);
        assertTrue(world.contains(bear));
    }

    @Test
    public void bear_eats_berries () throws Exception {
        setUp();
        Location bearLocation = new Location (3, 5);
        Bear bear = new Bear(world, false, bearLocation);
        world.setTile(bearLocation, bear);

        Location berry_location = new Location (3, 6);
        Berry berry = new Berry();
        world.setTile(berry_location, berry);

        for (int i = 0; i < 25; i++) {
            berry.act(world);
        }

        int max_steps = 30;
        boolean eaten = false;
        for (int i = 0; i <= max_steps; i++) {
            bear.restoreEnergyForTesting();
            if (!world.contains(berry)) break;
            bear.act(world);
            if (berry.getBerries() == 0) { eaten = true; break; }
        }

        assertTrue(eaten);
    }
}
