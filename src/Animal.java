import itumulator.simulator.Actor;
import itumulator.world.World;

public class Animal implements Actor {
    protected World world;
    protected double energy;

    public Animal(World world, double energy) {
        this.world = world;
        this.energy = energy;
    }

    @Override
    public void act(World world) {
        if (energy <= 0) {
            world.delete(this); // The animal dies when it does not have any energy left.
        }
    }
}
