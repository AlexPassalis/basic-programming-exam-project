package Test;
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
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Wolf(world, false, new Den()));
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        testAnimalDeath(new Wolf(world, false, new Den()));
    }

    @Test
    public void follower_follows_alpha() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        Location denLocation = new Location(5, 5);
        world.setTile(denLocation, den);

        Wolf alpha = new Wolf(world, false, den);
        Wolf follower = new Wolf(world, false, den, alpha);
        alpha.addFollower(follower);

        Location alphaStart = new Location(2, 2);
        Location followerStart = new Location (2, 4);
        world.setTile(alphaStart, alpha);
        world.setTile(followerStart, follower);
        alpha.act(world);
        follower.act(world);

        Location alphaNew = world.getLocation(alpha);
        Location followerNew = world.getLocation(follower);

        int oldDistance = Math.abs(alphaStart.getX() - followerStart.getX()) +
                Math.abs(alphaStart.getY() - followerStart.getY());

        int newDistance = Math.abs(alphaNew.getX() - followerNew.getX()) +
                Math.abs(alphaNew.getY() - followerNew.getY());

        assert newDistance < oldDistance;
    }

    @Test
    public void alpha_hunts_and_kills_rabbits() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        Location denLocation = new Location(5, 5);
        world.setTile(denLocation, den);
        Wolf alpha = new Wolf(world, false, den);
        Location alphaLocation = new Location (2, 2);
        world.setTile(alphaLocation, alpha);

        Rabbit rabbit = new Rabbit(world, false);
        Location rabbitLocation = new Location (2, 4);
        world.setTile(rabbitLocation, rabbit);

        alpha.setEnergy(10);
        int oldDistance = Math.abs(alphaLocation.getX() - rabbitLocation.getX()) +
                Math.abs(alphaLocation.getY() - rabbitLocation.getY());
        alpha.act(world);
        Location alphaNew = world.getLocation(alpha);
        int newDistance = Math.abs(alphaNew.getX() - rabbitLocation.getX()) +
                Math.abs(alphaNew.getY() - rabbitLocation.getY());
        assertTrue(newDistance < oldDistance);

        final int MAX_STEPS = 10;
        boolean rabbitRemoved = false;
        for (int i = 0; i < MAX_STEPS; i++) {
            if (!world.contains(rabbit)) {
                rabbitRemoved = true;
                break;
            }
            alpha.act(world);
        }
        assertFalse(world.contains(rabbit));
    }

    @Test
    public void alpha_enters_den_and_creates_pup () throws FileNotFoundException, IllegalAccessException, NoSuchFieldException {
        setUp();
        Den den = new Den();
        Location denLocation = new Location(5, 5);
        world.setTile(denLocation, den);

        Wolf alpha = new Wolf(world, false, den);
        Wolf follower = new Wolf (world, false, den, alpha);
        alpha.addFollower(follower);
        Location alphaLocation = new Location (2, 2);
        Location followerLocation = new Location (2, 3);

        world.setTile(alphaLocation, alpha);
        world.setTile(followerLocation, follower);
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
