package test;
import app.*;

import app.animal.Animal;
import app.animal.Rabbit;
import app.animal.Wolf;
import org.junit.jupiter.api.Test;
import itumulator.world.Location;
import java.io.FileNotFoundException;
import static org.junit.jupiter.api.Assertions.*;

public class TestWolf extends TestSuper {
    @Test
    public void gets_initialized() throws FileNotFoundException {
        getsInitialized("src/data/week-2/t2-1ab.txt", Wolf.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        animalDies(new Wolf(world, false, new Den()));
    }

    @Test
    public void follower_follows_alpha() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        Location den_location = new Location(5, 5);
        world.setTile(den_location, den);

        Wolf alpha = new Wolf(world, false, den);
        Location alpha_spawn_location = new Location(2, 2);
        world.setTile(alpha_spawn_location, alpha);

        Wolf follower = new Wolf(world, false, den, alpha);
        Location follower_spawn_location = new Location (2, 3);
        world.setTile(follower_spawn_location, follower);

        for (int i = 0; i < 10; i = i + 1) {
            program.simulate();
        }

        Location alpha_new_location = world.getLocation(alpha);
        Location follower_new_location = world.getLocation(follower);
        int distance = Math.abs(alpha_new_location.getX() - follower_new_location.getX()) +
                Math.abs(alpha_new_location.getY() - follower_new_location.getY());

        assertTrue(distance <= 3);
    }

    @Test
    public void alpha_hunts_and_kills_rabbits() throws FileNotFoundException {
        setUp();

        Den den = new Den();
        Location den_location = new Location(5, 5);
        world.setTile(den_location, den);

        Wolf alpha = new Wolf(world, false, den);
        Location alpha_spawn_location = new Location (4, 4);
        world.setTile(alpha_spawn_location, alpha);

        Rabbit rabbit = new Rabbit(world, false);
        Location rabbit_spawn_location = new Location (2, 2);
        world.setTile(rabbit_spawn_location, rabbit);

        int old_distance = Math.abs(alpha_spawn_location.getX() - rabbit_spawn_location.getX()) +
                Math.abs(alpha_spawn_location.getY() - rabbit_spawn_location.getY());

        for (int i = 0; i < 1; i = i + 1) {
            alpha.act(world);
        }
        Location alpha_new_location = world.getLocation(alpha);
        Location rabbit_new_location = world.getLocation(rabbit);
        int new_distance = Math.abs(alpha_new_location.getX() - rabbit_new_location.getX()) +
                Math.abs(alpha_new_location.getY() - rabbit_new_location.getY());
        assertTrue(new_distance < old_distance);

        for (int i = 0; i < 10; i  = i + 1) {
            alpha.act(world);
        }
        assertFalse(world.contains(rabbit));
    }

    @Test
    public void alpha_enters_den_and_creates_pup () throws FileNotFoundException, IllegalAccessException, NoSuchFieldException {
        setUp();
        Den den = new Den();
        Location den_location = new Location(5, 5);
        world.setTile(den_location, den);

        Wolf alpha = new Wolf(world, false, den);
        Wolf follower = new Wolf (world, false, den, alpha);
        alpha.addFollower(follower);
        Location alpha_spawn_location = new Location (2, 2);
        Location follower_spawn_location = new Location (2, 3);

        world.setTile(alpha_spawn_location, alpha);
        world.setTile(follower_spawn_location, follower);
        int initialFollowers = alpha.getFollowers().size();

        java.lang.reflect.Field worldField = Animal.class.getDeclaredField("world");
        worldField.setAccessible(true);
        worldField.set(alpha, world);
        worldField.set(follower, world);

        alpha.enterDenForReproduction(world.getLocation(alpha));
        follower.enterDenForReproduction(world.getLocation(follower));

        java.lang.reflect.Field counterField = Wolf.class.getDeclaredField("simulation_counts_reproducing");
        counterField.setAccessible(true);
        counterField.setInt(alpha, 1);
        counterField.setInt(follower, 1);

        alpha.act(world);
        follower.act(world);

        assertTrue(world.contains(alpha));
        int afterFollowers = alpha.getFollowers().size();
        assertTrue(afterFollowers > initialFollowers);

        int wolfCount = 0;
        for (Object e: world.getEntities().keySet()) if (e instanceof Wolf) wolfCount++;
        assertTrue(wolfCount >= initialFollowers + 2);
    }
}
