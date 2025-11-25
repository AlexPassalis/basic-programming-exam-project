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
        if (isAlpha) {
            this.followers = new ArrayList<>();
            this.alpha = null;
        } else {
            this.followers = null;
            this.alpha = null;
        }
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

    public Wolf getAlpha() {
        return alpha;
    }

    @Override
    public void act(World world) {
        move();
        super.act(world);
    }

    public void move() {
        Location current_location = world.getLocation(this);
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
            if (alpha != null && world.isOnTile(alpha)) {
                Location alpha_location = world.getLocation(alpha);

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
                }
            } else {
                // If no alpha or alpha not on tile, move randomly
                int randomIndex = new Random().nextInt(tiles.size());
                Location randomTile = tiles.get(randomIndex);
                world.move(this, randomTile);
            }
        }
    }

    // Setter method for testing purposes
    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
