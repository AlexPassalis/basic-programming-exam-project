package app.animal;

import app.Berry;
import app.Carcass;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.*;

public class Bear extends Predator {
    private Location spawn_location;
    private int territory_radius;

    public Bear(World world, boolean carcass_has_fungi, Location spawn_location) {
        super(world, carcass_has_fungi);
        this.spawn_location = spawn_location;
        this.territory_radius = 3;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) { // Don't act if having been removed e.g. eaten
            return;
        }

        super.act(world);
    }

    @Override
    protected void movementLogic() {
        Set<Location> territory_tiles = world.getSurroundingTiles(spawn_location, territory_radius);
        if (territory_tiles.size() < 1) {
            return;
        }

        Location current_location = world.getLocation(this);

        Location closest_wolf_location = null;
        int min_wolf_distance = Integer.MAX_VALUE;

        Location closest_deer_location = null;
        int min_deer_distance = Integer.MAX_VALUE;

        Location closest_rabbit_location = null;
        int min_rabbit_distance = Integer.MAX_VALUE;

        Location closest_carcass_location = null;
        int min_carcass_distance = Integer.MAX_VALUE;

        Location closest_berry_location = null;
        int min_berry_distance = Integer.MAX_VALUE;

        for (Location location : territory_tiles) {
            Object tile = world.getTile(location);

            if (tile instanceof Wolf) {
                int distance = calculateManhattanDistance(location, current_location);

                if (distance < min_wolf_distance) {
                    min_wolf_distance = distance;
                    closest_wolf_location = location;
                }
            }

            if (tile instanceof Deer) {
                int distance = calculateManhattanDistance(location, current_location);

                if (distance < min_deer_distance) {
                    min_deer_distance = distance;
                    closest_deer_location = location;
                }
            }

            if (tile instanceof Rabbit) {
                int distance = calculateManhattanDistance(location, current_location);

                if (distance < min_rabbit_distance) {
                    min_rabbit_distance = distance;
                    closest_rabbit_location = location;
                }
            }

            if (tile instanceof Carcass) {
                int distance = calculateManhattanDistance(location, current_location);

                if (distance < min_carcass_distance) {
                    min_carcass_distance = distance;
                    closest_carcass_location = location;
                }
            }

            if (tile instanceof Berry && ((Berry) tile).getBerries() > 0) {
                int distance = calculateManhattanDistance(location, current_location);

                if (distance < min_berry_distance) {
                    min_berry_distance = distance;
                    closest_berry_location = location;
                }
            }
        }

        // Movement priority: Wolf > Deer > Rabbit > Carcass > Berry > Random
        Location target_destination = null;

        if (closest_wolf_location != null) {
            target_destination = closest_wolf_location;
        } else if (closest_deer_location != null) {
            target_destination = closest_deer_location;
        } else if (closest_rabbit_location != null) {
            target_destination = closest_rabbit_location;
        } else if (closest_carcass_location != null) {
            target_destination = closest_carcass_location;
        } else if (closest_berry_location != null) {
            target_destination = closest_berry_location;
        }

        // Move one tile towards target or move randomly
        Set<Location> adjacent_tiles = getTilesInsideTerritory();
        if (adjacent_tiles.isEmpty()) {
            return;
        }

        Location next_tile = null;

        if (target_destination != null) {
            // Find adjacent tile closest to target
            int best_distance = Integer.MAX_VALUE;

            for (Location tile : adjacent_tiles) {
                int distance = calculateManhattanDistance(tile, target_destination);

                if (distance < best_distance) {
                    best_distance = distance;
                    next_tile = tile;
                }
            }
        } else {
            // No target, move randomly
            List<Location> list = new ArrayList<>(adjacent_tiles);
            int index = new Random().nextInt(list.size());
            next_tile = list.get(index);
        }

        if (next_tile != null) {
            Object tile_at_new_location = world.getTile(next_tile);

            if (tile_at_new_location instanceof Deer || tile_at_new_location instanceof Rabbit || tile_at_new_location instanceof Wolf) {
                Animal animal = (Animal) tile_at_new_location;
                kill(animal);
                return;
            } else if (tile_at_new_location instanceof Carcass) {
                Carcass carcass = (Carcass) tile_at_new_location;
                eatCarcass(carcass, this);
                return;
            } else if (tile_at_new_location instanceof Berry) {
                Berry berry = (Berry) tile_at_new_location;
                int berry_count = berry.getBerries();
                if (berry_count > 0) {
                    eatBerries(berry, berry_count);
                    return;
                }
            }

            if (world.isTileEmpty(next_tile)) {
                world.move(this, next_tile);
            }
        }
    }

    private Set<Location> getTilesInsideTerritory() {
        Location current_location = world.getLocation(this);
        Set<Location> adjacent_tiles = world.getSurroundingTiles(current_location);
        Set<Location> valid_tiles = new HashSet<>();

        for (Location tile : adjacent_tiles) {
            int distance = calculateManhattanDistance(tile, spawn_location);
            if (distance <= territory_radius) {
                valid_tiles.add(tile);
            }
        }

        return valid_tiles;
    }

    @Override
    protected void loseEnergyForMoving() {
        int energy_lost_for_moving = 2;
        energy = energy - energy_lost_for_moving;
    }

    public void restoreEnergyForTesting() { // Method for testing purposes
        this.energy = 100;
    }

    private void eatBerries(Berry berry, int berry_count) {
        int energy_per_berry = 20;
        energy = energy + (berry_count * energy_per_berry);
        berry.getEaten(world);
    }
}
