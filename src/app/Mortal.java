package app;

import itumulator.world.World;

/**
 * The Mortal interface defines the behavior for entities that can be removed
 * from the simulation, typically upon death or depletion of energy.
 */
public interface Mortal {

    /**
     * Removes the entity from the world.
     */
    default void die(World world) {
        world.delete(this);
    }
}
