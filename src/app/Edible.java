package app;

import itumulator.world.World;

public interface Edible extends Mortal {
    default void getEaten(World world) {
        die(world);
    };
}
