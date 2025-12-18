package test;

import app.*;
import app.animal.Deer;
import app.animal.Wolf;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeer extends TestSuper {
    @Test
    public void gets_initialized() throws FileNotFoundException {
        getsInitialized("src/data/week-4/t4-1a.txt", Deer.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        animalDies(new Deer(world, false));
    }

    @Test
    public void eats_grass() throws FileNotFoundException {
        setUp();

        Location deer_location = new Location(5, 5);
        Deer deer = new Deer(world, false);
        world.setTile(deer_location, deer);

        Location grass_location = new Location(5, 6);
        Grass grass = new Grass();
        world.setTile(grass_location, grass);

        double initial_energy = deer.getEnergy();

        deer.act(world);

        assertTrue(deer.getEnergy() > initial_energy);
        assertFalse(world.containsNonBlocking(grass_location));
    }

    @Test
    public void eats_berry() throws FileNotFoundException {
        setUp();

        Location deer_location = new Location(5, 5);
        Deer deer = new Deer(world, false);
        world.setTile(deer_location, deer);

        Location berry_location = new Location(5, 6);
        Berry berry = new Berry();
        world.setTile(berry_location, berry);

        double initial_energy = deer.getEnergy();

        deer.act(world);

        assertTrue(deer.getEnergy() > initial_energy);
    }

    @Test
    public void eats_fungi() throws FileNotFoundException {
        setUp();

        Location deer_location = new Location(5, 5);
        Deer deer = new Deer(world, false);
        world.setTile(deer_location, deer);

        Location fungi_location = new Location(5, 6);
        Fungi fungi = new Fungi(20);
        world.setTile(fungi_location, fungi);

        double initial_energy = deer.getEnergy();

        deer.act(world);

        assertTrue(deer.getEnergy() > initial_energy);
    }

    @Test
    public void does_not_eat_carcass_when_not_starving() throws FileNotFoundException {
        setUp();

        Location deer_location = new Location(5, 5);
        Deer deer = new Deer(world, false);
        deer.setEnergy(50);
        world.setTile(deer_location, deer);

        Location carcass_location = new Location(5, 6);
        Carcass carcass = new Carcass(false);
        world.setTile(carcass_location, carcass);

        deer.act(world);

        assertTrue(world.getTile(carcass_location) instanceof Carcass);
    }

    @Test
    public void eats_carcass_when_starving() throws FileNotFoundException {
        setUp();

        Location deer_location = new Location(5, 5);
        Deer deer = new Deer(world, false);
        deer.setEnergy(25);
        world.setTile(deer_location, deer);

        Location carcass_location = new Location(5, 6);
        Carcass carcass = new Carcass(false);
        world.setTile(carcass_location, carcass);

        double initial_energy = deer.getEnergy();

        deer.act(world);

        assertTrue(deer.getEnergy() > initial_energy);
    }

    @Test
    public void flees_from_predator() throws FileNotFoundException {
        setUp();

        Location deer_location = new Location(5, 5);
        Deer deer = new Deer(world, false);
        world.setTile(deer_location, deer);

        Location wolf_location = new Location(5, 7);
        Wolf wolf = new Wolf(world, false, null);
        world.setTile(wolf_location, wolf);

        deer.act(world);

        Location new_deer_location = world.getLocation(deer);
        int distance_before = Math.abs(5 - 5) + Math.abs(5 - 7);
        int distance_after = Math.abs(new_deer_location.getX() - wolf_location.getX()) +
                            Math.abs(new_deer_location.getY() - wolf_location.getY());

        assertTrue(distance_after >= distance_before);
    }

    @Test
    public void can_reproduce() throws FileNotFoundException {
        setUp();

        Location location1 = new Location(5, 5);
        Location location2 = new Location(5, 6);

        Deer deer1 = new Deer(world, false);
        Deer deer2 = new Deer(world, false);

        world.setTile(location1, deer1);
        world.setTile(location2, deer2);

        deer1.setAge(4);
        deer1.setEnergy(70);
        deer2.setAge(4);
        deer2.setEnergy(70);

        int initial_deer_count = countDeer();

        deer1.reproduce();

        int final_deer_count = countDeer();

        assertEquals(initial_deer_count + 1, final_deer_count);
    }

    @Test
    public void cannot_reproduce_without_enough_energy() throws FileNotFoundException {
        setUp();

        Location location1 = new Location(5, 5);
        Location location2 = new Location(5, 6);

        Deer deer1 = new Deer(world, false);
        Deer deer2 = new Deer(world, false);

        world.setTile(location1, deer1);
        world.setTile(location2, deer2);

        deer1.setAge(4);
        deer1.setEnergy(50);
        deer2.setAge(4);
        deer2.setEnergy(50);

        int initial_deer_count = countDeer();

        deer1.reproduce();

        int final_deer_count = countDeer();

        assertEquals(initial_deer_count, final_deer_count);
    }

    @Test
    public void cannot_reproduce_without_enough_age() throws FileNotFoundException {
        setUp();

        Location location1 = new Location(5, 5);
        Location location2 = new Location(5, 6);

        Deer deer1 = new Deer(world, false);
        Deer deer2 = new Deer(world, false);

        world.setTile(location1, deer1);
        world.setTile(location2, deer2);

        deer1.setAge(2);
        deer1.setEnergy(70);
        deer2.setAge(2);
        deer2.setEnergy(70);

        int initial_deer_count = countDeer();

        deer1.reproduce();

        int final_deer_count = countDeer();

        assertEquals(initial_deer_count, final_deer_count);
    }

    private int countDeer() {
        int count = 0;
        for (Object entity : world.getEntities().keySet()) {
            if (entity instanceof Deer) {
                count++;
            }
        }
        return count;
    }
}
