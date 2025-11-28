import itumulator.world.World;
import itumulator.world.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class Wolf extends Animal {
    Den den;
    boolean isAlpha;
    List<Wolf> followers; // For alpha wolves
    Wolf alpha; // For follower wolves

    Wolf(World world, Den den) {
        super(world, 100);
        this.den = den;
        this.isAlpha = false;
        this.followers = new ArrayList<>();
        this.alpha = null;

    }

    Wolf(World world, Den den, boolean isAlpha) {
        super(world, 100);
        this.den = den;
        this.isAlpha = isAlpha;
        this.followers = new ArrayList<>();
        this.alpha = null;



    }

    public boolean isAlpha() {
        return isAlpha;
    }

    public void addFollower(Wolf wolf) {
        if (isAlpha && followers != null && !followers.contains(wolf)) {
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

    public Wolf getAlpha() {
        return alpha;
    }

    @Override
    public void act(World world) {
        // clear it early to avoid later calls into world with a removed object.
        if (!isAlpha && alpha != null) {
            try {
                if (world.getLocation(alpha) == null) {
                    alpha = null;
                }
            } catch (Exception e) {
                // In case world.getLocation throws for a removed object, clear reference.
                alpha = null;
            }
        }
        // **** FIX END ****

        // **** FIX START: surround risky move() with a try/catch so a single bad access
        // does not silently corrupt the world or the UI. We log and attempt minimal recovery.
        try {
            move();
        } catch (IllegalArgumentException e) {
            System.err.println("Wolf.act: caught IllegalArgumentException during move(): " + e.getMessage());
            e.printStackTrace();

            // Attempt a simple recovery: clear alpha reference (common root cause),
            // and clear followers if this wolf is alpha and is being removed soon.
            try {
                if (alpha != null && world.getLocation(alpha) == null) {
                    alpha = null;
                }
            } catch (Exception ignored) {}

            // If this wolf is alpha and about to die, make sure followers don't keep referencing it.
            if (isAlpha && followers != null) {
                for (Wolf w : followers) {
                    try { w.alpha = null; } catch (Exception ignored) {}
                }
            }

            // Stop further actions for this tick.
            return;
        }

        double energy_reduction = 1.5;
        energy = energy - energy_reduction;

        // If the alpha is about to die, delete all its followers first
        if (isAlpha && energy <= 0 && followers != null) {
            if (!followers.isEmpty()) {
                for (Wolf wolf : followers) {
                    wolf.alpha = null;
                    try {
                        wolf.alpha = null;
                        world.delete(wolf);
                    } catch (Exception ignored) {
                        // Wolf was already deleted from the world, skip it
                    }
                }
                followers.clear();
            }
        }
        for (Wolf wolf: followers) {
            try {
                world.delete(wolf);
            } catch (IllegalArgumentException e) {
            }
        }
        try {
            followers.clear();
        } catch (Exception ignored) {}
        super.act(world);
    }

    public void move() {
        Location current_location = world.getLocation(this);

        if (isAlpha) {
            Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location, 2);

            Location closest_alpha_location = null;
            int min_distance_alpha = Integer.MAX_VALUE;

            Location closest_rabbit_location = null;
            int min_distance_rabbit = Integer.MAX_VALUE;

            for (Location location : surrounding_tiles) {
                Object tile = world.getTile(location);

                if (tile instanceof Wolf) {
                    Wolf other_wolf = (Wolf) tile;
                    if (other_wolf.isAlpha() && other_wolf != this) {
                        int dx = Math.abs(location.getX() - current_location.getX());
                        int dy = Math.abs(location.getY() - current_location.getY());
                        int distance = dx + dy;

                        if (distance < min_distance_alpha) {
                            min_distance_alpha = distance;
                            closest_alpha_location = location;
                        }
                    }
                }

                if (tile instanceof Rabbit) {
                    int dx = Math.abs(location.getX() - current_location.getX());
                    int dy = Math.abs(location.getY() - current_location.getY());
                    int distance = dx + dy;

                    if (distance < min_distance_rabbit) {
                        min_distance_rabbit = distance;
                        closest_rabbit_location = location;
                    }
                }
            }

            if (!isHungry() && closest_alpha_location != null) {
                // Check if alpha is on adjacent tile (can fight it)
                if (min_distance_alpha == 1) {
                    Wolf other_alpha = (Wolf) world.getTile(closest_alpha_location);
                    fightAlpha(other_alpha);
                    return;
                }

                // Move towards the other alpha
                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile : empty_tiles) {
                    int dx = Math.abs(tile.getX() - closest_alpha_location.getX());
                    int dy = Math.abs(tile.getY() - closest_alpha_location.getY());
                    int distance = dx + dy;

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

            if (isHungry() && closest_rabbit_location != null) {
                // Check if rabbit is on adjacent tile (can eat it)
                if (min_distance_rabbit == 1) {
                    Rabbit rabbit = (Rabbit) world.getTile(closest_rabbit_location);
                    eatRabbit(rabbit);
                    return;
                }

                // Move towards the rabbit
                Set<Location> empty_tiles = world.getEmptySurroundingTiles(current_location);
                Location best_tile = null;
                int best_distance = Integer.MAX_VALUE;

                for (Location tile : empty_tiles) {
                    int dx = Math.abs(tile.getX() - closest_rabbit_location.getX());
                    int dy = Math.abs(tile.getY() - closest_rabbit_location.getY());
                    int distance = dx + dy;

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

        Set<Location> neighbour_empty_tiles = world.getEmptySurroundingTiles(current_location);

        if (neighbour_empty_tiles.isEmpty()) {
            return;
        }

        List<Location> tiles = new ArrayList<>(neighbour_empty_tiles);

        if (isAlpha) {
            // Alpha moves randomly
            int randomIndex = new Random().nextInt(tiles.size());
            Location randomTile = tiles.get(randomIndex);
            world.move(this, randomTile);
        } else {
            // Follower moves towards alpha
            if (alpha != null) {
                Location alpha_location = null;
                try {
                    alpha_location = world.getLocation(alpha);
                } catch (Exception e) {
                    alpha_location = null;
                }
                if (alpha_location != null) {
                    Location closest_tile = null;
                    int min_distance = Integer.MAX_VALUE;
                    for (Location tile : tiles) {
                        int dx = Math.abs(tile.getX() - alpha_location.getX());
                        int dy = Math.abs(tile.getY() - alpha_location.getY());
                        int distance = dx + dy;

                        if (distance < min_distance) {
                            min_distance = distance;
                            closest_tile = tile;
                        }
                    }

                    if (closest_tile != null) {
                        world.move(this, closest_tile);
                        return;
                    }
                } else {
                    alpha = null;
                    // If no alpha or alpha not on tile, move randomly

                }
            }
            int randomIndex = new Random().nextInt(tiles.size());
            Location randomTile = tiles.get(randomIndex);
            world.move(this, randomTile);
        }
    }
    // Setter method for testing purposes
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    private void eatRabbit(Rabbit rabbit){
        world.delete(rabbit);
        energy = energy + 50;
    }

    private boolean isHungry() {
        return energy < 50;
    }

    private void fightAlpha(Wolf other_alpha) {
        // The alpha with the least energy dies
        if (this.energy < other_alpha.energy) {
            world.delete(this);
        } else if (other_alpha.energy < this.energy) {
            world.delete(other_alpha);
        } else {
            // If both have equal energy, randomly choose one to die
            if (Math.random() < 0.5) {
                world.delete(this);
            } else {
                world.delete(other_alpha);
            }
        }
    }
}