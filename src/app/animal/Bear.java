package app.animal;

import app.Berry;
import app.Carcass;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

/**
 * Bear:
 * - Stays within a Manhattan-radius territory of its spawn location
 * - Hunts in priority order: Wolf > Rabbit > Carcass > Berry
 * - Eats berries only when it reaches them
 */
public class Bear extends Predator {

    private final Location spawnLocation;
    private static final int TERRITORY_RADIUS = 3;
    private boolean hasEatenBerry = false;

    public Bear(World world, boolean carcassHasFungi, Location spawnLocation) {
        super(world, carcassHasFungi);
        this.spawnLocation = spawnLocation;
    }

    @Override
    public void act(World world) {
        if (!world.isOnTile(this)) {
            return;
        }
        super.act(world);
    }

    // --------------------------------------------------
    // MOVEMENT LOGIC
    // --------------------------------------------------
    @Override
    protected void movementLogic() {

        Location current = world.getLocation(this);

        // 1) Eat berries if standing on them
        Object currentNonBlocking = world.getNonBlocking(current);
        if (currentNonBlocking instanceof Berry) {
            Berry berry = (Berry) currentNonBlocking;
            if (berry.getBerries() > 0) {
                eatBerries(berry);
                hasEatenBerry = true;
                return;
            }
        }

        // 2) Build territory (safe, bounded)
        Set<Location> territory = getTerritoryTiles();

        // 3) Find closest targets
        Location closestWolf = null;
        Location closestRabbit = null;
        Location closestCarcass = null;
        Location closestBerry = null;

        int wolfDist = Integer.MAX_VALUE;
        int rabbitDist = Integer.MAX_VALUE;
        int carcassDist = Integer.MAX_VALUE;
        int berryDist = Integer.MAX_VALUE;

        for (Location loc : territory) {
            Object tile = world.getTile(loc);
            int dist = calculateManhattanDistance(current, loc);

            if (tile instanceof Wolf && dist < wolfDist) {
                wolfDist = dist;
                closestWolf = loc;
            }

            if (tile instanceof Rabbit && dist < rabbitDist) {
                rabbitDist = dist;
                closestRabbit = loc;
            }

            if (tile instanceof Carcass && dist < carcassDist) {
                carcassDist = dist;
                closestCarcass = loc;
            }

            if (tile instanceof Berry) {
                Berry berry = (Berry) tile;
                if (berry.getBerries() > 0 && dist < berryDist) {
                    berryDist = dist;
                    closestBerry = loc;
                }
            }
        }

        // 4) Choose target by priority
        Location target = null;

        if (closestWolf != null) {
            target = closestWolf;
        } else if (closestRabbit != null) {
            target = closestRabbit;
        } else if (closestCarcass != null) {
            target = closestCarcass;
        } else if (closestBerry != null) {
            target = closestBerry;
        }

        // 5) Get valid adjacent moves (stay in territory)
        Set<Location> neighbours = world.getSurroundingTiles(current);
        neighbours.retainAll(territory);

        if (neighbours.isEmpty()) {
            return;
        }

        // 6) Pick next tile
        Location next = null;

        if (target != null) {
            int best = Integer.MAX_VALUE;
            for (Location loc : neighbours) {
                int d = calculateManhattanDistance(loc, target);
                if (d < best) {
                    best = d;
                    next = loc;
                }
            }
        } else {
            List<Location> list = new ArrayList<>(neighbours);
            next = list.get(new Random().nextInt(list.size()));
        }

        if (next == null) {
            return;
        }

        // 7) Interact with tile
        Object tile = world.getTile(next);

        if (tile instanceof Rabbit || tile instanceof Wolf) {
            kill((Animal) tile);
            world.move(this, next);
            return;
        }

        if (tile instanceof Carcass) {
            eatCarcass((Carcass) tile, this);
        }

        if (tile instanceof Berry) {
            Berry berry = (Berry) tile;
            if (berry.getBerries() > 0) {
                eatBerries(berry);
                return;
            }
        }

        world.move(this, next);
    }


    // TERRITORY

    private Set<Location> getTerritoryTiles() {
        Set<Location> tiles = new HashSet<>();
        int size = world.getSize();

        for (int dx = -TERRITORY_RADIUS; dx <= TERRITORY_RADIUS; dx++) {
            for (int dy = -TERRITORY_RADIUS; dy <= TERRITORY_RADIUS; dy++) {
                if (Math.abs(dx) + Math.abs(dy) <= TERRITORY_RADIUS) {
                    int x = spawnLocation.getX() + dx;
                    int y = spawnLocation.getY() + dy;

                    if (x >= 0 && x < size && y >= 0 && y < size) {
                        tiles.add(new Location(x, y));
                    }
                }
            }
        }
        return tiles;
    }


    // ENERGY

    @Override
    protected void loseEnergyForMoving() {
        energy -= 5;
    }

    public void restoreEnergyForTesting() {
        energy = 100;
    }


    // BERRIES
 
    private void eatBerries(Berry berry) {
        if (energy >= 100) {
            return;
        }
        energy = Math.min(100, energy + 30);
        berry.getEaten(world);
    }
}