import itumulator.simulator.Actor;
import java.util.Random;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.util.ArrayList;
import itumulator.world.Location;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Rabbit implements Actor {
private int hunger = 15;

    @Override
    public void act(World world) {

        hunger = hunger -2;

        if (hunger <= 0) {
            world.delete(this);
            return;
        }

    }

    Location rabbitLoc = world.getLocation(this);

        for (Object grasstile : world.getObjectsOnTile(rabbitLoc) {
        if (grasstile instanceof Grass) {
            world.delete(grasstile);
            eat();
            break; // stopper løkken så kaninen ikke kan spise uendeligt fra 1 tile.
        }


        public void eat() {
            energy + 5;
        }
    }


}
