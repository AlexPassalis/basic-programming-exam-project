package test;

import app.*;
import app.animal.Animal;
import app.animal.Rabbit;
import app.animal.Wolf;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestSuper {
    public World world;
    public boolean rabbit_can_die;

    // Overloaded setUp method - defaults to empty-world.txt if no filepath provided
    public void setUp() throws FileNotFoundException {
        setUp("src/data/empty-world.txt");
    }

    public void setUp(String filepath) throws FileNotFoundException {
        Main.main(new String[]{filepath, "true"}); //We pass the full file path to load the map
        world = Main.getWorld(); //Retrieves the World instance created by Main
    }

    @AfterEach
    void tearDown() {
        world = null; //Resets the world, to prevent data or state from test affecting next test
    }

    // Helper method to test if an entity can be initialized
    protected void testInitialization(Object entity, Class<?> entityClass) {
        // Create the entity at a specific location
        Location location = new Location(0, 0);
        world.setTile(location, entity);

        // Retrieves all entities currently in the world map
        Map<Object, Location> entities = world.getEntities();
        int count = 0;

        // Iterate through all entities to count how many are instances of the specified class
        for (Object e : entities.keySet()) {
            if (entityClass.isInstance(e)) {
                count++;
            }
        }

        // Assert that exactly one entity of the specified type exists in the world
        assertEquals(1, count);
    }

    // Helper method to test if an animal dies when energy reaches 0
    protected void testDeath(Actor actor, Class<?> animalClass) {
        // Create the animal at the center of the world
        Location center = new Location(5, 5);
        world.setTile(center, actor);

        // Set the actor's energy to 0 to trigger death
        if (actor instanceof Animal) {
            ((Animal) actor).setEnergy(0);
        }

        // Trigger the actor's act method to check if it dies due to no energy
        actor.act(world);

        // Check if any animals of this type exist in the world
        Map<Object, Location> entities = world.getEntities();
        boolean hasAnimal = false;
        for (Object entity : entities.keySet()) {
            if (animalClass.isInstance(entity)) {
                hasAnimal = true;
                break;
            }
        }

        // Assert that no animals of this type remain (the animal died)
        assertFalse(hasAnimal);
    }
}
