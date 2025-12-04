import org.junit.jupiter.api.Test;
import itumulator.world.Location;
import java.io.FileNotFoundException;

public class TestWolf extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        testInitialization(new Wolf(den), Wolf.class);
    }

    @Test
    public void can_die() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        testDeath(new Wolf(den), Wolf.class);
    }

    @Test
    public void follower_follows_alpha() throws FileNotFoundException {
        setUp();
        Den den = new Den();
        Location denLocation = new Location(5, 5);
        world.setTile(denLocation, den);

        Wolf alpha = new Wolf(den);
        Wolf follower = new Wolf(den, alpha);
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
}
