package test;
import app.*;

import app.animal.Rabbit;
import itumulator.world.Location;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestRabbit extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Rabbit(world, false));
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        testAnimalDeath(new Rabbit(world, false));
    }

    @Test
    public void eats_grass() throws FileNotFoundException {
        setUp();

        // Create a rabbit and grass at adjacent locations
        Location rabbit_location = new Location(5, 5);
        Location grass_location = new Location(5, 6); // Adjacent to rabbit

        Rabbit rabbit = new Rabbit(world, false);
        Grass grass = new Grass();

        world.setTile(rabbit_location, rabbit);
        world.setTile(grass_location, grass);

        // Record rabbit's initial energy
        double initial_energy = rabbit.getEnergy();

        // Make the rabbit eat the grass
        rabbit.eat(grass);

        // Verify rabbit's energy increased after eating
        double energy_after_eating = rabbit.getEnergy();
        assertTrue(energy_after_eating > initial_energy);

        // Verify the grass was deleted from the world
        Map<Object, Location> entities = world.getEntities();
        boolean grass_exists = false;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                grass_exists = true;
                break;
            }
        }
        assertFalse(grass_exists);
    }

    @Test
    public void age_determines_energy_loss() throws FileNotFoundException {
        setUp();

        // Create two rabbits with different ages to test age-energy relationship
        Location location1 = new Location(5, 5);
        Location location2 = new Location(7, 7);

        Rabbit young_rabbit = new Rabbit(world, false);
        Rabbit old_rabbit = new Rabbit(world, false);

        world.setTile(location1, young_rabbit);
        world.setTile(location2, old_rabbit);

        // Set rabbits to different ages but same energy
        young_rabbit.setAge(1);
        young_rabbit.setEnergy(100);

        old_rabbit.setAge(5);
        old_rabbit.setEnergy(100);

        // Record initial energies
        double young_initial_energy = young_rabbit.getEnergy();
        double old_initial_energy = old_rabbit.getEnergy();

        // Let both rabbits act once
        young_rabbit.act(world);
        old_rabbit.act(world);

        // Get energies after one act
        double young_energy_after = young_rabbit.getEnergy();
        double old_energy_after = old_rabbit.getEnergy();

        // Calculate energy loss for each rabbit
        double young_energy_loss = young_initial_energy - young_energy_after;
        double old_energy_loss = old_initial_energy - old_energy_after;

        // Expected energy loss: age * 1.25
        // Young rabbit (age 1): 1 * 1.25 = 1.25
        // Old rabbit (age 5): 5 * 1.25 = 6.25
        assertEquals(1.25, young_energy_loss, 0.01);
        assertEquals(6.25, old_energy_loss, 0.01);

        // Verify that older rabbits lose MORE energy per act than younger rabbits
        assertTrue(old_energy_loss > young_energy_loss);
    }

    @Test
    public void can_reproduce() throws FileNotFoundException {
        setUp();

        // Create two rabbits next to each other
        Location location1 = new Location(5, 5);
        Location location2 = new Location(5, 6); // Adjacent to location1

        Rabbit rabbit1 = new Rabbit(world, false);
        Rabbit rabbit2 = new Rabbit(world, false);

        world.setTile(location1, rabbit1);
        world.setTile(location2, rabbit2);

        // Set both rabbits to meet reproduction requirements (age >= 5, energy >= 50)
        rabbit1.setAge(5);
        rabbit1.setEnergy(100);
        rabbit2.setAge(5);
        rabbit2.setEnergy(100);

        // Count rabbits before reproduction attempt
        Map<Object, Location> entities_before = world.getEntities();
        int rabbits_before = 0;
        for (Object entity : entities_before.keySet()) {
            if (entity instanceof Rabbit) {
                rabbits_before++;
            }
        }
        assertEquals(2, rabbits_before);

        // Record initial energies
        double rabbit1_initial_energy = rabbit1.getEnergy();
        double rabbit2_initial_energy = rabbit2.getEnergy();

        // Call reproduce on both rabbits until one successfully reproduces
        // This accounts for the hashCode comparison in reproduce() that determines which rabbit can reproduce
        boolean reproduction_happened = false;
        int max_attempts = 10;
        int attempts = 0;

        while (!reproduction_happened && attempts < max_attempts) {
            rabbit1.reproduce();
            rabbit2.reproduce();
            attempts++;

            // Check if reproduction happened by seeing if energy decreased
            if (rabbit1.getEnergy() < rabbit1_initial_energy - 25 ||
                rabbit2.getEnergy() < rabbit2_initial_energy - 25) {
                reproduction_happened = true;
            }
        }

        // Count rabbits after reproduction
        Map<Object, Location> entities_after = world.getEntities();
        int rabbits_after = 0;
        for (Object entity : entities_after.keySet()) {
            if (entity instanceof Rabbit) {
                rabbits_after++;
            }
        }

        // Assert that exactly one new rabbit was born
        assertEquals(rabbits_before + 1, rabbits_after);
    }
    @Test
    public void goes_to_burrow_at_night () throws FileNotFoundException {
        setUp();
        Location burrowLocation = new Location (4,4);
        world.setTile(burrowLocation, new Burrow());

        Location rabbitLocation = new Location (4, 6);
        Rabbit rabbit = new Rabbit(world, false);
        world.setTile(rabbitLocation, rabbit);

        world.setNight();
        rabbit.act(world);
        Location before = world.getLocation(rabbit);
        int oldDistance = Math.abs(before.getX() - burrowLocation.getX()) +
                Math.abs(before.getY() - burrowLocation.getY());

        if (!world.contains(rabbit)) {
            assertFalse(world.contains(rabbit));
            return;
        }
        Location after = world.getLocation(rabbit);
        int newDistance = Math.abs(after.getX() - burrowLocation.getX()) +
                Math.abs(after.getY() - burrowLocation.getY());

        assertTrue(newDistance < oldDistance);
    }
}
