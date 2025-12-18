package app.animal;

import app.Carcass;
import app.Den;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.*;

public class Wolf extends Predator {
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
        alpha.addFollower(this);
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

    public void setAlpha(Wolf alpha_wolf) {
        if (!isAlpha) {
            this.alpha = alpha_wolf;
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

        if (energy <= 0 || (!isAlpha && alpha == null)) {
            if (world.isOnTile(this)) {
                die();
            }
            return;
        }

        if (simulation_counts_reproducing > 0) {
            simulation_counts_reproducing = simulation_counts_reproducing - 1;
            if (simulation_counts_reproducing == 0) {
                exitDenAfterReproduction();
            }
        }

        if (!world.isOnTile(this)) { // Don't act if having been removed e.g. eaten or still in den
            return;
        }

        if (!is_reproduction_time) {
            double reproduction_chance = 0.03;
            double dice = new Random().nextDouble();
            if (dice < reproduction_chance) {
                is_reproduction_time = true;
            }
        }

        super.act(world);
    }

    @Override
    protected void movementLogic() {
        Location current_location = world.getLocation(this);
        Set<Location> adjacent_tiles = world.getSurroundingTiles(current_location);

        if (isAlpha) {
            for (Location tile : adjacent_tiles) {
                Object object = world.getTile(tile);

                if (object instanceof Deer) {
                    kill((Deer) object);
                    return;
                } else if (object instanceof Rabbit) {
                    kill((Rabbit) object);
                    return;
                } else if (object instanceof Carcass carcass) {
                    eatCarcass(carcass, this);
                    return;
                } else if (object instanceof Wolf otherWolf) {
                    if (isAlpha && otherWolf.isAlpha()) {
                        fightAlpha(otherWolf);
                        return;
                    }
                }
            }

            int territory_radius = 5;
            Set<Location> territory_tiles = world.getSurroundingTiles(current_location, territory_radius);
            if (!territory_tiles.isEmpty()) {
                Location closest_deer = null;
                int min_deer_distance = Integer.MAX_VALUE;
                Location closest_rabbit = null;
                int min_rabbit_distance = Integer.MAX_VALUE;
                Location closest_carcass = null;
                int min_carcass_distance = Integer.MAX_VALUE;

                for (Location location : territory_tiles) {
                    Object tile = world.getTile(location);

                    if (tile instanceof Deer) {
                        int distance = calculateManhattanDistance(location, current_location);
                        if (distance < min_deer_distance) {
                            min_deer_distance = distance;
                            closest_deer = location;
                        }
                    } else if (tile instanceof Rabbit) {
                        int distance = calculateManhattanDistance(location, current_location);
                        if (distance < min_rabbit_distance) {
                            min_rabbit_distance = distance;
                            closest_rabbit = location;
                        }
                    } else if (tile instanceof Carcass && isHungry()) {
                        int distance = calculateManhattanDistance(location, current_location);
                        if (distance < min_carcass_distance) {
                            min_carcass_distance = distance;
                            closest_carcass = location;
                        }
                    }
                }

                Location target = null;
                if (closest_deer != null) {
                    target = closest_deer;
                } else if (closest_rabbit != null) {
                    target = closest_rabbit;
                } else if (closest_carcass != null) {
                    target = closest_carcass;
                }

                if (target != null) {
                    Location next_tile = null;
                    int best_distance = Integer.MAX_VALUE;
                    for (Location tile : adjacent_tiles) {
                        int distance = calculateManhattanDistance(tile, target);
                        if (distance < best_distance) {
                            best_distance = distance;
                            next_tile = tile;
                        }
                    }
                    if (next_tile != null && world.isTileEmpty(next_tile)) {
                        world.move(this, next_tile);
                    }
                } else if (!adjacent_tiles.isEmpty()) {
                    List<Location> list = new ArrayList<>(adjacent_tiles);
                    Location randomTile = list.get(new Random().nextInt(list.size()));
                    if (world.isTileEmpty(randomTile)) {
                        world.move(this, randomTile);
                    }
                }
            }
        } else {
            if (alpha != null && world.contains(alpha) && world.isOnTile(alpha)) {
                Location alpha_location = world.getLocation(alpha);
                Location next_tile = null;
                int best_distance = Integer.MAX_VALUE;
                for (Location tile : adjacent_tiles) {
                    int distance = calculateManhattanDistance(tile, alpha_location);
                    if (distance < best_distance) {
                        best_distance = distance;
                        next_tile = tile;
                    }
                }
                if (next_tile != null && world.isTileEmpty(next_tile)) {
                    world.move(this, next_tile);
                }
            } else if (!adjacent_tiles.isEmpty()) {
                List<Location> list = new ArrayList<>(adjacent_tiles);
                Location randomTile = list.get(new Random().nextInt(list.size()));
                if (world.isTileEmpty(randomTile)) {
                    world.move(this, randomTile);
                }
            }
        }
    }

    @Override
    protected void loseEnergyForMoving() {
        double energy_reduction = 1;
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
            die();
        } else if (other_alpha.energy < this.energy) {
            if (other_alpha.followers != null) {
                for (Wolf follower : other_alpha.followers) {
                    follower.setAlpha(this);
                    this.addFollower(follower);
                }
                other_alpha.followers.clear();
            }
            other_alpha.die();
        } else {
            if (Math.random() < 0.5) {
                if (this.followers != null) {
                    for (Wolf follower : this.followers) {
                        follower.setAlpha(other_alpha);
                        other_alpha.addFollower(follower);
                    }
                    this.followers.clear();
                }
                die();
            } else {
                if (other_alpha.followers != null) {
                    for (Wolf follower : other_alpha.followers) {
                        follower.setAlpha(this);
                        this.addFollower(follower);
                    }
                    other_alpha.followers.clear();
                }
                other_alpha.die();
            }
        }
    }

    @Override
    public void die() {
        if (isAlpha && followers != null) {
            for (Wolf follower : followers) {
                follower.alpha = null;
            }
            followers.clear();
        }
        super.die();
    }

    public void enterDenForReproduction(Location location) {
        reproducing_location = location;
        world.remove(this);

        simulation_counts_reproducing = 15;
    }

    public void exitDenAfterReproduction() {
        if (reproducing_location == null) {
            return;
        }

        if (world.isTileEmpty(reproducing_location)) {
            world.setTile(reproducing_location, this);
            reproducing_location = null;
        } else {
            Set<Location> exit_tiles = world.getEmptySurroundingTiles(reproducing_location);
            if (!exit_tiles.isEmpty()) {
                Location exit_location = exit_tiles.iterator().next();
                world.setTile(exit_location, this);
                reproducing_location = null;
            } else {
                simulation_counts_reproducing = 1;
                return;
            }
        }

        if (isAlpha) {
            is_reproduction_time = false;

            Wolf pup = new Wolf(world, false, den, this);
            addFollower(pup);

            Location den_location = world.getLocation(den);
            Set<Location> pup_exit_tiles = world.getEmptySurroundingTiles(den_location);
            if (!pup_exit_tiles.isEmpty()) {
                Location pup_location = pup_exit_tiles.iterator().next();
                world.setTile(pup_location, pup);
            } else {
                world.setTile(den_location, pup);
            }
        }

        simulation_counts_reproducing = 0;
    }
}
