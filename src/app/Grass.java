package app;

import itumulator.simulator.Actor;
import java.util.Random;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.util.ArrayList;
import itumulator.world.Location;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Grass implements Actor, NonBlocking, Edible {
    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) {
            return;
        }
        
        spread(world);
    }

    private void spread(World world) {
        double chance_to_spread = 0.05; // 5% chance for the Grass to spread
        double dice = new Random().nextDouble();
        if (chance_to_spread < dice) {
            return;
        }

        Location grassLocation = world.getLocation(this);
        Set<Location> neighbours = world.getSurroundingTiles(grassLocation);
        List<Location> list = new ArrayList<>();

        for (Location neighbour : neighbours) {
            if (!world.containsNonBlocking(neighbour) && world.isTileEmpty(neighbour)) {
                list.add(neighbour);
            }
        }

        if (list.isEmpty()) {
            return;
        }

        int rand = new Random().nextInt(list.size()); // Find a random integer in our ArrayList of empty tiles.
        Location location = list.get(rand); // Gets a random empty tile from the ArrayList.
        world.setTile(location, new Grass());
   }
}
