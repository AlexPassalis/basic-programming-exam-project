package app.animal;

import app.Carcass;
import app.Den;
import app.Edible;
import itumulator.world.World;
import itumulator.world.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class Wolf extends Predator implements Edible {
    private Den den;
    private boolean isAlpha;
    private Wolf alpha;
    private List<Wolf> followers;
    private boolean is_reproduction_time;
    private int simulation_counts_reproducing;
    private Location reproducing_location;

    public Wolf(World world, boolean carcass_has_fungi, Den den, Wolf alpha) {
        super(world, carcass_has_fungi);
        this.den = den;
        this.isAlpha = false;
        this.alpha = alpha;
        this.followers = null;
        this.is_reproduction_time = false;
        this.simulation_counts_reproducing = 0;
    }

    public Wolf(World world, boolean carcass_has_fungi, Den den) {
        super(world, carcass_has_fungi);
        this.den = den;
        this.isAlpha = true;
        this.followers = new ArrayList<>();
        this.is_reproduction_time = false;
        this.simulation_counts_reproducing = 0;
    }

    public boolean isAlpha() {
        return isAlpha;
    }

    public void addFollower(Wolf wolf) {
        if (isAlpha && !followers.contains(wolf)) {
            followers.add(wolf);
        }
    }

    public void setAlpha(Wolf alphaWolf) {
        if (!isAlpha) {
            this.alpha = alphaWolf;
        }
    }

    public List<Wolf> getFollowers() {
        return followers;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this) && reproducing_location == null) {
            return;
        }

        if (simulation_counts_reproducing > 0) {
            simulation_counts_reproducing = simulation_counts_reproducing - 1;
            if (simulation_counts_reproducing == 0 && isAlpha) {
                exitDenAfterReproduction();
            }
        }

        if (!world.isOnTile(this)) { // Don't act if having been removed e.g. eaten or still in den
            return;
        }

        if (!is_reproduction_time) {
            double reproduction_chance = 0.03;
            double dice = new Random().nextDouble(1);
            if (dice < reproduction_chance) {
                is_reproduction_time = true;
            }
        }

        if (energy <= 0) {
            if (isAlpha) {
                for (Wolf wolf : followers) {
                    wolf.die();
                }
            }
            die();
        }

        super.act(world);
    }

    @Override
    protected void movementLogic() {
        Location current_location = world.getLocation(this);

        if (isAlpha) {
            if (!followers.isEmpty() && is_reproduction_time) {
                Location den_location = world.getLocation(den);

                // Check if wolf reached the den
                if (current_location.equals(den_location)) {
                    enterDenForReproduction(current_location);
                    return;
                }

                // Find the shortest path to den by moving to the adjacent tile closest to den
                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile : empty_tiles) {
                    int distance = calculateManhattanDistance(tile, den_location);

                    if (distance < best_distance) {
                        best_distance = distance;
                        best_tile = tile;
                    }
                }

                if (best_tile != null) {
                    world.move(this, best_tile);
                }

                return;
            }

            Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location, 5);

            Location closest_alpha_location = null;
            int min_distance_alpha = Integer.MAX_VALUE;

            Location closest_rabbit_location = null;
            int min_distance_rabbit = Integer.MAX_VALUE;

            Location closest_carcass_location = null;
            int min_distance_carcass = Integer.MAX_VALUE;

            for (Location location : surrounding_tiles) {
                Object tile = world.getTile(location);

                if (tile instanceof Wolf) {
                    Wolf other_wolf = (Wolf) tile;
                    if (other_wolf.isAlpha() && other_wolf != this) {
                        int distance = calculateManhattanDistance(location, current_location);

                        if (distance < min_distance_alpha) {
                            min_distance_alpha = distance;
                            closest_alpha_location = location;
                        }
                    }
                }

                if (tile instanceof Rabbit) {
                    int distance = calculateManhattanDistance(location, current_location);

                    if (distance < min_distance_rabbit) {
                        min_distance_rabbit = distance;
                        closest_rabbit_location = location;
                    }
                }

                if (tile instanceof Carcass) {
                    int distance = calculateManhattanDistance(location, current_location);

                    if (distance < min_distance_carcass) {
                        min_distance_carcass = distance;
                        closest_carcass_location = location;
                    }
                }
            }
            if (closest_rabbit_location != null) {
                if (min_distance_rabbit == 1) {
                    Rabbit rabbit = (Rabbit) world.getTile(closest_rabbit_location);
                    kill(rabbit);
                    return;
                }
                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile: empty_tiles) {
                    int distance = calculateManhattanDistance(tile, closest_rabbit_location);
                    if (distance < best_distance) {
                        best_distance = distance;
                        best_tile = tile;
                    }
                }
                if (best_tile !=null) {
                    world.move(this, best_tile);
                    return;
                }
            }

            if (isHungry() && closest_carcass_location != null) {
                if (min_distance_carcass == 1) {
                    Carcass carcass = (Carcass) world.getTile(closest_carcass_location);
                    eatCarcass(carcass, this);
                    return;
                }

                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile : empty_tiles) {
                    int distance = calculateManhattanDistance(tile, closest_carcass_location);

                    if (distance < best_distance) {
                        best_distance = distance;
                        best_tile = tile;
                    }
                }

                if (best_tile != null) {
                    world.move(this, best_tile);
                    return;
                }
            }

            if (closest_rabbit_location != null) {
                if (min_distance_rabbit == 1) {
                    Rabbit rabbit = (Rabbit) world.getTile(closest_rabbit_location);
                    kill(rabbit);
                    return;
                }

                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile : empty_tiles) {
                    int distance = calculateManhattanDistance(tile, closest_rabbit_location);

                    if (distance < best_distance) {
                        best_distance = distance;
                        best_tile = tile;
                    }
                }

                if (best_tile != null) {
                    world.move(this, best_tile);
                    return;
                }
            }

            if (!isHungry() && closest_alpha_location != null) {
                if (min_distance_alpha == 1) {
                    Wolf other_alpha = (Wolf) world.getTile(closest_alpha_location);
                    fightAlpha(other_alpha);
                    return;
                }

                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile : empty_tiles) {
                    int distance = calculateManhattanDistance(tile, closest_alpha_location);

                    if (distance < best_distance) {
                        best_distance = distance;
                        best_tile = tile;
                    }
                }

                if (best_tile != null) {
                    world.move(this, best_tile);
                    return;
                }
            }
        }

        if (isAlpha) {
            // Alpha with no followers moves randomly
            Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
            if (empty_tiles.isEmpty()) {
                return;
            }
            List<Location> tiles_list = new ArrayList<>(empty_tiles);
            int randomIndex = new Random().nextInt(tiles_list.size());
            Location randomTile = tiles_list.get(randomIndex);
            world.move(this, randomTile);
            return;
        }
        // Follower movement logic
        // Check if alpha is reproducing - move to den instead
        if (alpha != null && alpha.is_reproduction_time) {
            Location den_location = world.getLocation(den);

            // Check if follower reached the den
            if (current_location.equals(den_location)) {
                enterDenForReproduction(current_location);
                return;
            }

            // Move towards den
            Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
            Location closest_tile = null;
            int min_distance = Integer.MAX_VALUE;
            for (Location tile : empty_tiles) {
                int distance = calculateManhattanDistance(tile, den_location);

                if (distance < min_distance) {
                    min_distance = distance;
                    closest_tile = tile;
                }
            }

            if (closest_tile != null) {
                world.move(this, closest_tile);
            }
        } else if (alpha != null && world.contains(alpha) && world.isOnTile(alpha)) {
            Location alpha_location = world.getLocation(alpha);

            Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
            Location closest_tile = null;
            int min_distance = Integer.MAX_VALUE;
            for (Location tile : empty_tiles) {
                int distance = calculateManhattanDistance(tile, alpha_location);

                if (distance < min_distance) {
                    min_distance = distance;
                    closest_tile = tile;
                }
            }

            if (closest_tile != null) {
                world.move(this, closest_tile);
            }
        } else if (alpha == null || !world.contains(alpha) || !world.isOnTile(alpha)){
            Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
            if (!empty_tiles.isEmpty()) {
                List<Location> empty_list = new ArrayList<>(empty_tiles);
                int randomIndex = new Random().nextInt(empty_list.size());
                Location randomTile = empty_list.get(randomIndex);
                world.move(this, randomTile);
            }
        }
    }

    @Override
    protected void loseEnergyForMoving() {
        int energy_reduction = 2;
        energy = energy - energy_reduction;
    }

    private boolean isHungry() {
        return energy < 70;
    }

    @Override
    public void eatCarcass(Carcass carcass, Animal animal) {
        double meat_available = carcass.getMeatAmount();
        if (meat_available <= 0) {
            return;
        }

        int meat_amount = 4;
        int meat_consumed = carcass.eatMeat(meat_amount);
        if (meat_consumed > 0) {
            double energy_per_meat_consumed = 2; // 2 energy per meat consumed from the carcass
            double energy_addition = energy_per_meat_consumed * meat_consumed;
            energy = energy + energy_addition;

            // Followers gain the same energy
            for (Wolf follower : followers) {
                follower.energy = follower.energy + energy_addition;
            }
        }
    }

    private void fightAlpha(Wolf other_alpha) {
        if (this.energy < other_alpha.energy) {
            if (this.followers != null) {
                for (Wolf follower : this.followers) {
                    follower.setAlpha(other_alpha);
                    other_alpha.addFollower(follower);
                }
                this.followers.clear();
            }
            world.delete(this);
        } else if (other_alpha.energy < this.energy) {
            if (other_alpha.followers != null) {
                for (Wolf follower : other_alpha.followers) {
                    follower.setAlpha(this);
                    this.addFollower(follower);
                }
                other_alpha.followers.clear();
            }
            world.delete(other_alpha);
        } else {
            if (Math.random() < 0.5) {
                if (this.followers != null) {
                    for (Wolf follower : this.followers) {
                        follower.setAlpha(other_alpha);
                        other_alpha.addFollower(follower);
                    }
                    this.followers.clear();
                }
                world.delete(this);
            } else {
                if (other_alpha.followers != null) {
                    for (Wolf follower : other_alpha.followers) {
                        follower.setAlpha(this);
                        this.addFollower(follower);
                    }
                    other_alpha.followers.clear();
                }
                world.delete(other_alpha);
            }
        }
    }

    public void enterDenForReproduction(Location location) {
        reproducing_location = location;
        world.remove(this);

        simulation_counts_reproducing = 15;
    }

    public void exitDenAfterReproduction() {
        if (reproducing_location != null) {
            Location den_location = world.getLocation(den);
            Set<Location> exit_tiles = world.getEmptySurroundingTiles(den_location);

            // Re-add alpha
            if (!exit_tiles.isEmpty()) {
                Location exit_location = exit_tiles.iterator().next();
                world.setTile(exit_location, this);
                exit_tiles.remove(exit_location);
                reproducing_location = null;
            }

            // Re-add all followers
            if (followers != null) {
                for (Wolf follower : followers) {
                    if (follower.reproducing_location != null) {
                        if (!exit_tiles.isEmpty()) {
                            Location follower_exit = exit_tiles.iterator().next();
                            world.setTile(follower_exit, follower);
                            exit_tiles.remove(follower_exit);
                            follower.reproducing_location = null;
                            follower.simulation_counts_reproducing = 0;
                        }
                    }
                }

                // Create new pup
                Wolf pup = new Wolf(world,false, den, this);
                addFollower(pup);

                if (!exit_tiles.isEmpty()) {
                    Location pup_location = exit_tiles.iterator().next();
                    world.setTile(pup_location, pup);
                } else {
                    // fallback: place pup on den tile
                    world.setTile(den_location, pup);
                }
            }
        }

        is_reproduction_time = false;
        simulation_counts_reproducing = 0;
    }
}
