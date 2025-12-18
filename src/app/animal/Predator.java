package app.animal;

import itumulator.world.World;

public class Predator extends Animal {
    public Predator(World world, boolean carcass_has_fungi) {
        super(world, carcass_has_fungi);
    }

    public void kill(Animal animal) {
        animal.die();
    }
}
