package test;

import app.*;
import app.animal.Animal;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuper {
    public Program program;
    public World world;

    public void setUp(String filepath) throws FileNotFoundException {
        Main.main(new String[]{filepath, "true"});
        program = Main.getProgram();
        world = Main.getWorld();
    }

    // Overloaded setUp method - defaults to empty-world.txt if no filepath provided
    public void setUp() throws FileNotFoundException {
        setUp("src/data/empty-world.txt");
    }

    @AfterEach
    void tearDown() {
        world = null; //Resets the world, to prevent data or state from test affecting next test
    }

    // Helper method to check whether the instance gets initialized in the world
    public void getsInitialized(String filepath, Class<?> entityClass) throws FileNotFoundException {
        setUp(filepath);
        Map<Object, Location> entities = world.getEntities();

        boolean has_atleast_one_entity = false;
        for (Object _entity : entities.keySet()) {
            if (entityClass.isInstance(_entity)) {
               has_atleast_one_entity = true;
            }
        }

        assertTrue(has_atleast_one_entity);
    }

    // Helper method to test if an animal dies when energy reaches 0
    public void animalDies(Animal animal) {
        Location location = new Location(0, 0);
        world.setTile(location, animal);

        animal.setEnergy(0); // Set the animal's energy to 0
        animal.act(world); // act to call the die method

        assertFalse(world.contains(animal));
    }
}
