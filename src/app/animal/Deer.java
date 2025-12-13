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
        if (!world.isOnTile(this)) return;

        simulation_counts = simulation_counts + 1;
        if (simulation_counts % 10 == 0) {
            age = age + 1;
        }

        super.act(world);
        reproduce();
    }

    @Override
    protected void movementLogic() {
        Location current_location = world.getLocation(this);
        Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location);
        if (surrounding_tiles.isEmpty()) {
            return;
        }

        List<Location> options = filterUnsafeTiles(new ArrayList<>(surrounding_tiles));
        if (options.isEmpty()) options = new ArrayList<>(surrounding_tiles);

        Location best = chooseFoodTile(options);
        if (best == null) best = options.get(new Random().nextInt(options.size()));

        world.move(this, best);
        eatIfPossible(best);
    }

    private Location findNearbyPredator(int radius) {
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
        if (predator == null) {
            return null;
        }

        Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
        if (empty_tiles.isEmpty()) return null;

        Location best = null;
        int bestDistance = Integer.MIN_VALUE;

        for (Location tile : empty_tiles) {
            int dx = Math.abs(tile.getX() - predator.getX());
            int dy = Math.abs(tile.getY() - predator.getY());
            int distance = dx + dy;

            if (distance > bestDistance) {
                bestDistance = distance;
                best = tile;
            }
        }
        return best;
    }

    @Override
    protected void loseEnergyForMoving() {
        int energy_reduction = 2;
        energy = energy - energy_reduction;
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
        if (isStarving()) {
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
        for (Location option : options) {
            Object food = world.getNonBlocking(option);
            if (food instanceof Fungi  || food instanceof Berry || food instanceof Grass) { // Prefers Fungi > Berry > Grass
                return option;
            }
        }
        return null;
    }

    public void reproduce() {
        int minAge = 4;
        if (age < minAge) return;

        double minEnergy = 70;
        if (energy < minEnergy) return;

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