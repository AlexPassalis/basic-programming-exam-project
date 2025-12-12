package app;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Deer extends Animal {

    private int age;
    private int simulation_counts;

    public Deer(boolean carcass_has_fungi) {
        super(carcass_has_fungi);
        this.age = 0;
        this.simulation_counts = 0;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) return;

        this.world = world;

        simulation_counts++;
        if (simulation_counts % 10 == 0) age++;

        super.act(world);   // movementLogic() + energy drain + death check
        reproduce();
    }

    @Override
    protected void movementLogic() {
        Location current = world.getLocation(this);

        Set<Location> empty = world.getEmptySurroundingTiles(current);
        if (empty.isEmpty()) return;

        List<Location> options = filterUnsafeTiles(new ArrayList<>(empty));
        if (options.isEmpty()) options = new ArrayList<>(empty);

        Location best = chooseFoodTile(options);
        if (best == null) best = options.get(new Random().nextInt(options.size()));

        world.move(this, best);
        eatIfPossible(best);
    }

    @Override
    protected void loseEnergyForMoving() {
        double baseCost = 1.5;
        double ageCost = 0.35 * age;
        energy -= (baseCost + ageCost);
    }

    private void eatIfPossible(Location tile) {
        Object food = world.getNonBlocking(tile);

        if (food instanceof Grass) {
            world.delete(food);
            energy += 20;
        } else if (food instanceof Berry) {
            world.delete(food);
            energy += 25;
        } else if (food instanceof Fungi) {
            world.delete(food);
            energy += 30;
        }
    }

    // Unik adfærd undgår carcasses når den ikke sulter
    private List<Location> filterUnsafeTiles(List<Location> tiles) {
        if (isStarving()) return tiles; // hvis hjorten er sulten, så returner den alle tiles, også carcasses.

        List<Location> safe = new ArrayList<>();
        for (Location t : tiles) {
            if (!isNearCarcass(t)) safe.add(t);
        }
        return safe;
    }

    private boolean isNearCarcass(Location loc) {
        for (Location a : world.getSurroundingTiles(loc, 1)) {
            Object tile = world.getTile(a);
            if (tile instanceof Carcass) return true;
        }
        return false;
    }

    private boolean isStarving() {
        return energy < 30;
    }

    private Location chooseFoodTile(List<Location> options) {
        for (Location t : options) {
            Object food = world.getNonBlocking(t);
            if (food instanceof Grass) return t;
            if (food instanceof Berry) return t;
            if (food instanceof Fungi) return t;
        }
        return null;
    }

    public void reproduce() {
        int minAge = 4;
        if (age < minAge) return;

        double minEnergy = 70;
        if (energy < minEnergy) return;

        Location loc = world.getLocation(this);

        Deer partner = null;
        for (Location l : world.getSurroundingTiles(loc, 1)) {
            Object o = world.getTile(l);
            if (o instanceof Deer d && d.age >= minAge && d.energy >= minEnergy) {
                partner = d;
                break;
            }
        }
        if (partner == null) return;

        if (this.hashCode() < partner.hashCode()) return;

        Set<Location> empty = world.getEmptySurroundingTiles(loc);
        if (empty.isEmpty()) return;

        List<Location> tiles = new ArrayList<>(empty);
        Location babyLoc = tiles.get(new Random().nextInt(tiles.size()));

        world.setTile(babyLoc, new Deer(false));

        energy -= 35;
        partner.energy -= 35;
    }
}