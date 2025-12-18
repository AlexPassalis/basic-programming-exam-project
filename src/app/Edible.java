package app;

import itumulator.world.World;

/**
 * The Edible interface represents objects that can be eaten.
 * When an edible object is eaten, it is removed from the world.
 * This interface is used by food sources such as grass, berries,
 * fungi and carcasses.
 */
public interface Edible extends Mortal {

    /**
     * Removes the object from the world when it is eaten.
     * By default, this calls the die method from the Mortal interface
     * @param world the world in which the object exists
     */
    default void getEaten(World world) {
        die(world);
    };
}
