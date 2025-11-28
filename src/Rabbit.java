import itumulator.simulator.Actor;
import itumulator.world.World;
import java.util.Random;
import java.util.ArrayList;
import itumulator.world.Location;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Rabbit implements Actor {
    private World world;
    private int age;
    private double energy;
    private Burrow burrow;
    private Location sleeping_location;
    private int simulation_counts;

    Rabbit(World world) {
        this.world = world;
        age = 0;
        energy = 100;
        burrow = null;
        sleeping_location = null;
        simulation_counts = 0;
    }

    private Burrow getClosestBurrow() {
        Map<Object, Location> entities_in_world = world.getEntities(); // Get all the entities in the world.
        Set<Burrow> burrows = new HashSet<>();
        for (Object entity : entities_in_world.keySet()) {
            if (entity instanceof Burrow) {
                burrows.add((Burrow) entity); // If it is a burrow, add it to the set.
            }
        }

        if (burrows.isEmpty()) {
            return null; // If there are no burrows, return null.
        }

        Location rabbit_location = world.getLocation(this); // Get the location of the rabbit.

        Burrow closest_burrow = null;
        int min_distance = Integer.MAX_VALUE;
        for (Burrow borr : burrows) {
            Location burrow_location = entities_in_world.get(borr);
            int dx = Math.abs(rabbit_location.getX() - burrow_location.getX());
            int dy = Math.abs(rabbit_location.getY() - burrow_location.getY());

            int distance = dx + dy;
            if (distance < min_distance) { // If this burrow is closer, update the closest_burrow variable.
                min_distance = distance;
                closest_burrow = borr;
            }
        }

        return closest_burrow; // Return the borrow that is closest in the world.
    }

    @Override
    public void act(World world) {
        if (simulation_counts == 1) {
            burrow = getClosestBurrow(); // Attach the closest borrow to the rabbit.
        }

        if (!world.isNight()) {
            this.wakeUp(); // Wake the rabbit up if it is not night anymore.
        }

        if (sleeping_location == null) {
            move(); // Move the rabbit, if it is not sleeping.
        }

        double energy_multiplier = 1.25;
        if (sleeping_location == null) { // Rabbit is awake.
            energy = energy - (age * energy_multiplier); // The rabbit losses energy from being awake.
            reproduce(); // The rabbit can reproduce when awake.
        } else { // Rabbit is sleeping.
            energy = energy * (1 + energy_multiplier); // The rabbit gains energy from being asleep.
        }

        if (energy <= 0) {
            world.delete(this); // The rabbit dies when it does not have any energy left.
        }

        simulation_counts = simulation_counts + 1; // Count how many program simulations the rabbit has been alive for.

        if (simulation_counts % 10 == 0) { // Age the rabbit by 1 year every 10 program simulations.
            age = age + 1;
        }
    }

    public void move() {
        Location current_location = world.getLocation(this); // Get the current location of the rabbit.
        Set<Location> neighbour_empty_tiles = world.getEmptySurroundingTiles(current_location); // Get all the empty surrounding tiles.

        if (neighbour_empty_tiles.isEmpty()) {
            return; // If there are no empty surrounding tiles, let the rabbit stay where it is at.
        }

        List<Location> tiles = new ArrayList<>(neighbour_empty_tiles);
        boolean is_night = world.isNight();

        if (burrow != null && is_night) { // If the rabbit has a Burrow and it is night, move towards it.
            Location burrow_location = world.getLocation(burrow);

            Location closest_tile = null;
            int min_distance = Integer.MAX_VALUE;
            for (Location tile : tiles) { // Find the closest tile towards the Burrow.
                int dx = Math.abs(tile.getX() - burrow_location.getX());
                int dy = Math.abs(tile.getY() - burrow_location.getY());
                int distance = dx + dy;

                if (distance < min_distance) {
                    min_distance = distance;
                    closest_tile = tile;
                }
            }

            if (closest_tile != null && closest_tile.equals(burrow_location)) { // When the Rabbit gets to its Borrow, sleep.
                this.sleep(current_location);
            } else if (closest_tile != null) { // Move the Rabbit towards its Burrow.
                world.move(this, closest_tile);
            }

        } else {
            int randomIndex = new Random().nextInt(tiles.size());
            Location randomTile = tiles.get(randomIndex);
            world.move(this, randomTile); // Move towards a random nerby tile.

            if (!is_night) { // If it is night and the tile the rabbit wants to move to is Grass, eat it.
                Object grass_tile = world.getNonBlocking(randomTile);
                if (grass_tile instanceof Grass) {
                    eat((Grass) grass_tile);
                }
            }
        }
    }

    public void eat(Grass grass_tile) {
        world.delete(grass_tile);
        energy = energy + 20;
    }

    public void reproduce() {
        if (sleeping_location != null) {
            return;
        }

        int min_reproduction_age = 5;
        if (age < min_reproduction_age) {
            return;
        }

        int min_energy_required = 50;
        if (energy < min_energy_required) {
            return;
        }

        Location location = world.getLocation(this);
        Set<Location> surrounding_tiles = world.getSurroundingTiles(location);
        Rabbit partner = null;

        for (Location surrounding_tile : surrounding_tiles) {
            Object actor = world.getTile(surrounding_tile);
            if (actor instanceof Rabbit) {
                Rabbit potential_partner = (Rabbit) actor;

                // Make sure the partner meets the requirements to have a baby as well.
                if (potential_partner.getAge() >= min_reproduction_age && potential_partner.getEnergy() >= min_energy_required) {
                    partner = potential_partner;
                    break;
                }
            }
        }

        if (partner == null) {
            return;
        }

        if (this.hashCode() < partner.hashCode()) { // The rabbit with the biggest hasCode gets to reproduce.
            return;
        }

        Set<Location> empty_tiles = world.getEmptySurroundingTiles(location);
        if (empty_tiles.isEmpty()) {
            return;
        }

        List<Location> tiles = new ArrayList<>(empty_tiles);
        int randomIndex = new Random().nextInt(tiles.size());
        Location baby_location = tiles.get(randomIndex);

        world.setTile(baby_location, new Rabbit(world));

        reproductionEnergyCost();
        partner.reproductionEnergyCost();
    }

    public int getAge() {
        return age;
    }

    public double getEnergy() {
        return energy;
    }

    // Setter methods for testing purposes
    public void setAge(int age) {
        this.age = age;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void reproductionEnergyCost() {
        energy = energy - 30; // Energy cost to reproduce.
    }

    public void sleep(Location location) {
        sleeping_location = location;
        world.remove(this);
    }

    public void wakeUp() {
        if (sleeping_location != null) {
            if (world.isTileEmpty(sleeping_location)) {
                world.setTile(sleeping_location, this);
                sleeping_location = null;
            } else {
                Set<Location> nearby_tiles = world.getEmptySurroundingTiles(sleeping_location);
                if (!nearby_tiles.isEmpty()) {
                    Location wake_location = nearby_tiles.iterator().next();
                    world.setTile(wake_location, this);
                    sleeping_location = null;
                }
            }
        }
    }
}
