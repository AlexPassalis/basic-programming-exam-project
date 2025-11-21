import itumulator.simulator.Actor;
import java.util.Random;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.util.ArrayList;
import itumulator.world.Location;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Rabbit implements Actor {
    private World world;
    private int age;
    private double energy;
    private int simulation_counts;

    Rabbit(World world) {
        this.world = world;
        age = 0;
        energy = 100;
    }

    @Override
    public void act(World world) {
        simulation_counts = simulation_counts + 1;
        move();

        energy = energy - (age * 0.75); // The older it gets, the more energy it misses per round to move.
        if (energy <= 0) {
            world.delete(this);
        }

        reproduce();

        if (simulation_counts % 10 == 0) { // Age by 1 for every 10 steps
            age = age + 1;
        }
    }

    public void move() {
        Location location = world.getLocation(this);
        Set<Location> neighbour_empty_tiles = world.getEmptySurroundingTiles(location);

        if (neighbour_empty_tiles.isEmpty()) {
            return;
        }

        List<Location> tiles = new ArrayList<>(neighbour_empty_tiles);
        int randomIndex = new Random().nextInt(tiles.size());
        Location randomTile = tiles.get(randomIndex);

        world.move(this, randomTile);

        Object grass_tile = world.getNonBlocking(randomTile);
        if (grass_tile instanceof Grass) {
            eat((Grass) grass_tile);
        }
    }

    public void eat(Grass grass_tile) {
        world.delete(grass_tile);
        energy = energy + 20;
    }

    public void reproduce() {
        int min_reproduction_age = 5;
        if (age < min_reproduction_age) {
            return;
        }

        int min_energy_required = 30;
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

        reduceEnergy();
        partner.reduceEnergy();
    }

    public int getAge() {
        return age;
    }

    public double getEnergy() {
        return energy;
    }

    public void reduceEnergy() {
        energy = energy - 15;
    }
}
