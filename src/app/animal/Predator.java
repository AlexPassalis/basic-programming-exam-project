package app.animal;

import itumulator.world.World;

/**
 * The Predator class represents animals that hunt other animals.
 * It extends the Animal class and adds behaviour for killing prey.
 * Predators such as wolves and bears inherit from this class.
 */
public class Predator extends Animal {
    /**
     * Creates a new predator.
     * @param world the world the predator exists in
     * @param carcass_has_fungi whether the predator should spawn fungi on death
     */
    public Predator(World world, boolean carcass_has_fungi) {
        super(world, carcass_has_fungi);
    }

    /**
     * Kills another animal.
     * This method causes the target animal to die,
     * which will results in a carcass being created
     * at its location.
     * @param animal the animal to be killed
     */
    public void kill(Animal animal) {
        animal.die();
    }
}
