package app;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Set;

public class Fungi implements Actor, NonBlocking, Edible, Mortal {
    int simulation_counts_left;

    public Fungi(int meat_amount) {
        int lifetime_per_meat_amount = 3;
        simulation_counts_left = meat_amount *  lifetime_per_meat_amount;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) {
            return;
        }

        if (simulation_counts_left <= 0) {
            die(world);
            return;
        }

        spread(world);
        simulation_counts_left = simulation_counts_left - 1;
    }

    private void spread(World world) {
        Location current_location = world.getLocation(this);

        int spread_radius = 2;
        Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location,spread_radius);
        for (Location tile : surrounding_tiles) {
            Object actor = world.getTile(tile);
            if (actor instanceof Carcass && !((Carcass) actor).hasFungi()) {
                ((Carcass) actor).getInfectedByFungi();
            }
        }
    }
}
