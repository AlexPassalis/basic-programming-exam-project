package test;
import app.*;

import org.junit.jupiter.api.Test;
import itumulator.world.Location;
import java.io.FileNotFoundException;
import static org.junit.jupiter.api.Assertions.*;

public class TestWolf extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        testInitialization(new Wolf(false, den), Wolf.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        testDeath(new Wolf(false, den), Wolf.class);
    }

    @Test
    public void follower_follows_alpha() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        Location denLocation = new Location(5, 5);
        world.setTile(denLocation, den);

        Wolf alpha = new Wolf(false, den);
        Wolf follower = new Wolf(false, den, alpha);
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
        Wolf alpha = new Wolf(false, den);
        Location alphaLocation = new Location (2, 2);
        world.setTile(alphaLocation, alpha);

        Rabbit rabbit = new Rabbit(false);
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
        Wolf alpha = new Wolf(false, den);
        Wolf follower = new Wolf (false, den, alpha);
        alpha.addFollower(follower);
        Location alphaLocation = new Location (2, 2);
        Location followerLocation = new Location (2, 3);
        world.setTile(alphaLocation, alpha);
        world.setTile(followerLocation, follower);
        int initialFollowers = alpha.getFollowers() == null ? 0 : alpha.getFollowers().size();
        var f = Wolf.class.getDeclaredField("is_reproduction_time");
        f.setAccessible(true);
        f.setBoolean(alpha, true);
        final int MAX_STEPS = 200;
        boolean pupProduced  = false;
        for (int step = 0; step < MAX_STEPS; step++) {
            try {alpha.act(world); } catch (IllegalArgumentException ignored) {}
            try {follower.act(world); } catch (IllegalArgumentException ignored) {}
            if (world.contains(alpha)) {
                int currentFollowers = alpha.getFollowers() == null ? 0 : alpha.getFollowers().size();
                if (currentFollowers == initialFollowers) {
                    pupProduced = true;
                    break;
                }
            }
        }
        assertTrue(pupProduced);
        int wolfCount = 0;
        for (Object e : world.getEntities().keySet()) {
            if (e instanceof Wolf) wolfCount++;
        }
        assertTrue(wolfCount >= initialFollowers + 2);
    }
}
