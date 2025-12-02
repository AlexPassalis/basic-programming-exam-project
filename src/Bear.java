import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.simulator.Simulator;
import java.util.*;

public class Bear implements Actor {
    private World world;
    private Location spawn_location;
    private double energy = 100;

    Bear(World world, Location spawn_location) {
        this.world = world;
        this.spawn_location = spawn_location;
    }

    @Override
    public void act(World world) {
        move(world);
        energy = energy - 10;
        if (energy <= 0) {
            world.delete(this);
        }
    }

    private void move(World world) {
        int territory_radius = 3;
        Location current_location = world.getLocation(this);
        Set<Location> territory_tiles = world.getSurroundingTiles(current_location, 3);

        if (territory_tiles.size() < 1) {
            return;
        }

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

            if (tile instanceof Bush) {
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
        Location target = null;

        if (closest_wolf_location != null) {
            target = closest_wolf_location;
        } else if (closest_rabbit_location != null) {
            target = closest_rabbit_location;
        } else if (closest_bush_location != null) {
            target = closest_bush_location;
        } else {
            // No food found, move randomly
            List<Location> list = new ArrayList<>(territory_tiles);
            int index = new Random().nextInt(list.size());
            target = list.get(index);
        }

        // Check what's on the target tile and eat if it's food
        Object tile_at_target = world.getTile(target);

        if (tile_at_target instanceof Wolf) {
            eatWolf((Wolf) tile_at_target);
        } else if (tile_at_target instanceof Rabbit) {
            eatRabbit((Rabbit) tile_at_target);
        } else if (tile_at_target instanceof Bush) {
            eatBerries((Bush) tile_at_target);
        }

        // Move to target after eating (if there was blocking food, it's now deleted)
        world.move(this, target);
    }

    private void eatWolf(Wolf wolf) {
        world.delete(wolf);
        energy = energy + 30;
    }
    private void eatRabbit(Rabbit rabbit) {
        world.delete(rabbit);
        energy = energy + 15;
    }
    private void eatBerries(Bush bush) {
        energy = energy + 30;
    }
}
