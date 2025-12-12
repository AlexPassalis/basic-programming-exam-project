package app.animal;

import app.Berry;
import itumulator.world.World;
import itumulator.world.Location;
import java.util.*;

public class Bear extends Predator {
    private Location spawn_location;

    public Bear(boolean carcass_has_fungi, Location spawn_location) {
        super(carcass_has_fungi);
        this.spawn_location = spawn_location;
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
        int territory_radius = 3;
        Set<Location> territory_tiles = world.getSurroundingTiles(spawn_location, territory_radius);
        if (territory_tiles.size() < 1) {
            return;
        }

        Location current_location = world.getLocation(this);

        // First, find the closest wolf in the territory
        Location closest_wolf_location = null;
        int min_wolf_distance = Integer.MAX_VALUE;

        Location closest_rabbit_location = null;
        int min_rabbit_distance = Integer.MAX_VALUE;

        Location closest_bush_location = null;
        int min_bush_distance = Integer.MAX_VALUE;

        for (Location location : territory_tiles) {
            Object tile = world.getTile(location);

            if (tile instanceof Wolf) {
                int dx = Math.abs(location.getX() - current_location.getX());
                int dy = Math.abs(location.getY() - current_location.getY());
                int distance = dx + dy;

                if (distance < min_wolf_distance) {
                    min_wolf_distance = distance;
                    closest_wolf_location = location;
                }
            }

            if (tile instanceof Rabbit) {
                int dx = Math.abs(location.getX() - current_location.getX());
                int dy = Math.abs(location.getY() - current_location.getY());
                int distance = dx + dy;

                if (distance < min_rabbit_distance) {
                    min_rabbit_distance = distance;
                    closest_rabbit_location = location;
                }
            }

            if (tile instanceof Berry && ((Berry) tile).hasBerries()) {
                int dx = Math.abs(location.getX() - current_location.getX());
                int dy = Math.abs(location.getY() - current_location.getY());
                int distance = dx + dy;

                if (distance < min_bush_distance) {
                    min_bush_distance = distance;
                    closest_bush_location = location;
                }
            }
        }

        // Movement priority: Wolf > Rabbit > Bush > Random
        Location target_destination = null;

        if (closest_wolf_location != null) {
            target_destination = closest_wolf_location;
        } else if (closest_rabbit_location != null) {
            target_destination = closest_rabbit_location;
        } else if (closest_bush_location != null) {
            target_destination = closest_bush_location;
        }

        // Move one tile towards target or move randomly
        Set<Location> adjacent_tiles = world.getEmptySurroundingTiles(current_location);

        if (adjacent_tiles.isEmpty()) {
            return;
        }

        Location next_tile = null;

        if (target_destination != null) {
            // Find adjacent tile closest to target
            int best_distance = Integer.MAX_VALUE;

            for (Location tile : adjacent_tiles) {
                int dx = Math.abs(tile.getX() - target_destination.getX());
                int dy = Math.abs(tile.getY() - target_destination.getY());
                int distance = dx + dy;

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
            world.move(this, next_tile);

            Object tile_at_new_location = world.getTile(next_tile);

            if (tile_at_new_location instanceof Rabbit || tile_at_new_location instanceof Wolf) {
                kill((Animal) tile_at_new_location);
            } if (tile_at_new_location instanceof Berry) {
                Berry berry = (Berry) tile_at_new_location;
                int berry_count = berry.getBerries();
                if (berry_count > 0) {
                    eatBerries(berry, berry_count);
                }
            }
        }
    }

    @Override
    protected void loseEnergyForMoving() {
        energy = energy - 10;
    }

    public void restoreEnergyForTesting() { // Method for testing purposes
        this.energy = 100;
    }

    private void eatBerries(Berry berry, int berry_count) {
        int energy_per_berry = 30;
        energy = energy + (berry_count * energy_per_berry);
        berry.getEaten(world);
    }
}
