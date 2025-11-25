import itumulator.world.World;
import itumulator.world.Location;

public class Wolf extends Animal {
    Wolf(World world) {
        super(world, 100);
    }

    @Override
    public void act(World world) {
        super.act(world);
    }

    public void move() {
        Location current_location = world.getLocation(this); // Get the current location of the wolf.
    }

    // Setter method for testing purposes
    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
