package app.animal;

import app.*;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Deer extends Animal {
    private int age;
    private int simulation_counts;

    public Deer(World world, boolean carcass_has_fungi) {
        super(world, carcass_has_fungi);
        this.age = 0;
        this.simulation_counts = 0;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) {
            return;
        }

        simulation_counts = simulation_counts + 1;
        if (simulation_counts % 10 == 0) {
            age = age + 1;
        }

        if (energy <= 0) {
            die();
            return;
        }

        reproduce();
        super.act(world);
    }

    @Override
    protected void movementLogic() {
        Location current_location = world.getLocation(this);

        Location predator_location = findNearbyPredator(2);
        if (predator_location != null) {
            Location flee_location = fleeFromPredator(predator_location);
            if (flee_location != null) {
                world.move(this, flee_location);
                eatIfPossible(flee_location);
                return;
            }
        }

        Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location);

        for (Location tile : surrounding_tiles) {
            Object food = world.getNonBlocking(tile);
            if (food instanceof Grass) {
                eatGrass((Grass) food);
                return;
            } else if (food instanceof Berry) {
                Berry berry = (Berry) food;
                if (berry.hasBerries()) {
                    eatBerries(berry);
                    return;
                }
            } else if (food instanceof Fungi) {
                eatFungi((Fungi) food);
                return;
            }
        }

        if (isStarving()) {
            for (Location tile : surrounding_tiles) {
                Object object = world.getTile(tile);
                if (object instanceof Carcass) {
                    eatCarcass((Carcass) object, this);
                    return;
                }
            }
        }
        if (surrounding_tiles.isEmpty()) {
            return;
        }

        List<Location> all_options = new ArrayList<>(surrounding_tiles);
        List<Location> safe_options = filterUnsafeTiles(all_options);

        List<Location> options;
        if (safe_options.isEmpty()) {
            options = all_options;
        } else {
            options = safe_options;
        }

        Location best_tile = chooseFoodTile(options);
        if (best_tile == null) {
            List<Location> empty_tiles = new ArrayList<>();
            for (Location option : options) {
                if (world.isTileEmpty(option)) {
                    empty_tiles.add(option);
                }
            }

            if (!empty_tiles.isEmpty()) {
                best_tile = empty_tiles.get(new Random().nextInt(empty_tiles.size()));
            }
        }

        if (best_tile != null) {
            Object tile_at_location = world.getTile(best_tile);

            if (isStarving() && tile_at_location instanceof Carcass) {
                eatCarcass((Carcass) tile_at_location, this);
                return;
            }

            if (world.isTileEmpty(best_tile)) {
                world.move(this, best_tile);
            }
            eatIfPossible(best_tile);
        }
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

    private Location fleeFromPredator(Location predator_location) {
        Location current_location = world.getLocation(this);
        Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
        if (empty_tiles.isEmpty()) {
            return null;
        }

        Location best_tile = null;
        int best_distance = Integer.MIN_VALUE;

        for (Location tile : empty_tiles) {
            int distance = calculateManhattanDistance(tile, predator_location);

            if (distance > best_distance) {
                best_distance = distance;
                best_tile = tile;
            }
        }

        return best_tile;
    }

    @Override
    protected void loseEnergyForMoving() {
        double energy_reduction = 1;
        energy = energy - energy_reduction;
    }

    private void eatIfPossible(Location tile) {
        Object food = world.getNonBlocking(tile);

        if (food instanceof Grass) {
            eatGrass((Grass) food);
        } else if (food instanceof Berry) {
            eatBerries((Berry) food);
        } else if (food instanceof Fungi) {
            eatFungi((Fungi) food);
        }
    }

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

    private boolean isNearCarcass(Location carcass_location) {
        for (Location carcass_tile : world.getSurroundingTiles(carcass_location, 1)) {
            Object tile = world.getTile(carcass_tile);
            if (tile instanceof Carcass) {
                return true;
            }
        }

        return false;
    }

    private boolean isStarving() {
        return energy < 30;
    }

    private Location chooseFoodTile(List<Location> options) {
        for (Location option : options) {
            Object food = world.getNonBlocking(option);
            if (food instanceof Fungi || food instanceof Grass) {
                return option;
            } else if (food instanceof Berry) {
                Berry berry = (Berry) food;
                if (berry.hasBerries()) {
                    return option;
                }
            }

            if (isStarving()) {
                Object blocking = world.getTile(option);
                if (blocking instanceof Carcass) {
                    return option;
                }
            }
        }
        return null;
    }

    public void reproduce() {
        int min_age = 6;
        if (age < min_age) {
            return;
        }

        double min_energy = 70;
        if (energy < min_energy) {
            return;
        }

        Location current_location = world.getLocation(this);

        Deer partner = null;
        for (Location location : world.getSurroundingTiles(current_location, 1)) {
            Object object = world.getTile(location);
            if (object instanceof Deer deer && deer.age >= min_age && deer.energy >= min_energy) {
                partner = deer;
                break;
            }
        }

        if (partner == null) {
            return;
        }

        if (this.hashCode() < partner.hashCode()) {
            return;
        }

        Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
        if (empty_tiles.isEmpty()) {
            return;
        }

        List<Location> tiles = new ArrayList<>(empty_tiles);
        Location baby_location = tiles.get(new Random().nextInt(tiles.size()));

        world.setTile(baby_location, new Deer(world, false));

        energy = energy - 35;
        partner.energy = partner.energy - 35;
    }

    public void setAge(int age) {
        this.age = age;
    }
}