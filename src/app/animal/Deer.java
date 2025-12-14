package app.animal;

import app.*;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Deer extends Animal implements Edible {
    private int age;
    private int simulation_counts;

    public Deer(World world, boolean carcass_has_fungi) {
        super(world, carcass_has_fungi);
        this.age = 0;
        this.simulation_counts = 0;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) return; // Checks if deer is dead. If it is indeed dead, then the program returns.

        simulation_counts = simulation_counts + 1; // For every 10 ticks or simulation counts, the deer will age 1 year.
        if (simulation_counts % 10 == 0) {
            age = age + 1;
        }

        super.act(world);
        reproduce();
    }

    /**
     * MovementLogic is a method for the deer's movement. It checks for the smartest tiles to pick.
     * It prefers safe tiles as determined by filerUnsafeTiles. If no safe tiles are available. All tiles are considered.
     */
    @Override
    protected void movementLogic() {
        Location current_location = world.getLocation(this);
        Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location);
        if (surrounding_tiles.isEmpty()) {
            return;
        }

        List<Location> allOptions = new ArrayList<>(surrounding_tiles);
        List<Location> safeOptions = filterUnsafeTiles(allOptions);

        List<Location> options;
        if (safeOptions.isEmpty()) {
            options = allOptions;
        } else {
            options = safeOptions;
        }

        Location best = chooseFoodTile(options);
        if (best == null) best = options.get(new Random().nextInt(options.size())); // If no food tiles are available, a random tile is chosen.

        world.move(this, best);
        eatIfPossible(best);
    }

    private Location findNearbyPredator(int radius) { // Checks surrounding tiles for animals that extends Predator, for a certain radius.
        Location current_location = world.getLocation(this);

        for (Location surrounding_location : world.getSurroundingTiles(current_location, radius)) {
            Object tile = world.getTile(surrounding_location);
            if (tile instanceof Predator) {
                return surrounding_location;
            }
        }

        return null;
    }

    private Location fleeFromPredator() {

        Location current_location = world.getLocation(this);
        Location predator = findNearbyPredator(3);
        if (predator == null) { // This is a method that works with the findNearbyPredator method and enables the deer to flee if it spots a predator.
            return null;
        }

        Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
        if (empty_tiles.isEmpty()) return null;

        Location best = null;
        int bestDistance = Integer.MIN_VALUE;

        for (Location tile : empty_tiles) {
            int distance = calculateManhattanDistance(tile, predator);

            if (distance > bestDistance) {
                bestDistance = distance;
                best = tile;
            }
        }
        return best;
    }

    /**
     * Every game tick / simulation count, the deer will lose 2 energy.
     */
    @Override
    protected void loseEnergyForMoving() {
        int energy_reduction = 2;
        energy = energy - energy_reduction;
    }

    private void eatIfPossible(Location tile) { // Provides a method for the amount of energy a deer gets when eating.
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
        } else if (food instanceof Carcass) { // Very rarely used.
            world.delete(food);
            energy += 30;
        }
    }

    private List<Location> filterUnsafeTiles(List<Location> tiles) {
        if (isStarving()) { // Provides a method that checks for carcasses. If a deer is not starving, it will avoid carcasses. If it is starving, it will become desperate and add carcasses as a food source.
            return tiles;
        }

        List<Location> safe = new ArrayList<>();
        for (Location tile : tiles) {
            if (!isNearCarcass(tile)) {
                safe.add(tile);
            }
        }

        return safe;
    }

    private boolean isNearCarcass(Location carcassLocation) {
        for (Location CarcassTile : world.getSurroundingTiles(carcassLocation, 1)) {
            Object tile = world.getTile(CarcassTile);
            if (tile instanceof Carcass)
                return true;
        }
        return false; // Provides a method that checks for nearby carcasses. It returns a true boolean if carcasses is on nearby tiles. Otherwise it returns false.
    }

    private boolean isStarving() {
        return energy < 30; // Checks if the deer is starving.
    }

    private Location chooseFoodTile(List<Location> options) {
        for (Location option : options) {
            Object food = world.getNonBlocking(option);
            if (food instanceof Fungi  || food instanceof Berry || food instanceof Grass || food instanceof Carcass) {
                return option;
            }
        } // Checks for possible tiles and returns the first tile that contains food. If no tiles contain food, return null.
        return null;
    }

    /**
     * Provides a method for  requirements for reproduction. The deer has to be a certain age and have a certain energy.
     * The new baby deer is spawned at a possible empty tile from the deer with the biggest hashCode.
     */
    public void reproduce() {
        int minAge = 4;
        if (age < minAge)
            return;

        double minEnergy = 70;
        if (energy < minEnergy)
            return;

        Location current_location = world.getLocation(this);

        Deer partner = null;
        for (Location l : world.getSurroundingTiles(current_location, 1)) {
            Object o = world.getTile(l);
            if (o instanceof Deer d && d.age >= minAge && d.energy >= minEnergy) {
                partner = d;
                break;
            }
        }
        if (partner == null)
            return;

        if (this.hashCode() < partner.hashCode())
            return;

        Set<Location> empty = world.getEmptySurroundingTiles(current_location);
        if (empty.isEmpty())
            return;

        List<Location> tiles = new ArrayList<>(empty);
        Location babyLocation = tiles.get(new Random().nextInt(tiles.size()));

        world.setTile(babyLocation, new Deer(world,false));

        energy = energy - 35;
        partner.energy = partner.energy - 35;
    }
}