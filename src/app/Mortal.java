package app;

import itumulator.world.World;

public interface Mortal {
    default void die(World world) {
        world.remove(this);
    }
}
