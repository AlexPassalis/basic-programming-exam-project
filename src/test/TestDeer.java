package test;

import app.Grass;
import app.animal.Deer;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestDeer extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Deer(world, false));
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        testAnimalDeath(new Deer(world, false));
    }

    @Test
    public void deer_eats_grass() throws FileNotFoundException {
        setUp();

        // Create a Deer and grass at adjacent locations
        Location Deer_location = new Location(5, 5);
        Location grass_location = new Location(5, 6); // Adjacent to deer

        Deer deer = new Deer(world, false);
        Grass grass = new Grass();

        world.setTile(Deer_location, deer);
        world.setTile(grass_location, grass);

        // Record deer's initial energy
        double initial_energy = deer.getEnergy();

        // Make the deer eat the grass
        Deer.eat();

        // Verify deer's energy increased after eating
        double energy_after_eating = deer.getEnergy();
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
    public void can_reproduce() throws FileNotFoundException {
        setUp();

        // Create two deers next to each other
        Location location1 = new Location(5, 5);
        Location location2 = new Location(5, 6); // Adjacent to location1

        Deer deer1 = new Deer(world, false);
        Deer deer2 = new Deer(world, false);

        world.setTile(location1, deer1);
        world.setTile(location2, deer2);

        // Set both deers to meet reproduction requirements (age >= 5, energy >= 50)
        deer1.setAge(5);
        deer1.setEnergy(100);
        deer2.setAge(5);
        deer2.setEnergy(100);

        // Count deers before reproduction attempt
        Map<Object, Location> entities_before = world.getEntities();
        int deers_before = 0;
        for (Object entity : entities_before.keySet()) {
            if (entity instanceof Deer) {
                deers_before++;
            }
        }
        assertEquals(2, deers_before);

        // Record initial energies
        double deer1_initial_energy = deer1.getEnergy();
        double deer2_initial_energy = deer2.getEnergy();

        // Call reproduce on both deers until one successfully reproduces
        // This accounts for the hashCode comparison in reproduce() that determines which deer can reproduce
        boolean reproduction_happened = false;
        int max_attempts = 10;
        int attempts = 0;

        while (!reproduction_happened && attempts < max_attempts) {
            deer1.reproduce();
            deer2.reproduce();
            attempts++;

            // Check if reproduction happened by seeing if energy decreased
            if (deer1.getEnergy() < deer1_initial_energy - 25 ||
                    deer2.getEnergy() < deer2_initial_energy - 25) {
                reproduction_happened = true;
            }
        }

        // Count deers after reproduction
        Map<Object, Location> entities_after = world.getEntities();
        int deers_after = 0;
        for (Object entity : entities_after.keySet()) {
            if (entity instanceof Deer) {
                deers_after++;
            }
        }

        // Assert that exactly one new deer was born
        assertEquals(deers_before + 1, deers_after);
    }
}






// Dyret skal kunne interagere med eksisterende elementer i økosystemet. Ådsler, planter og andre dyr.
// Test om det kan spise ådsler, planter

// Simuler dyrets livscyklus, herunder fødsel, vækst, reproduktion og død.
//  Implementer dyrets fødekæde og prædator-bytte forhold.
// Dyrets tilstedeværelse og adfærd skal kunne påvirke økosystemets balance og
//dynamik.