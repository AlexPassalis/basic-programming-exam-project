import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Carcass implements Actor, NonBlocking {
    private int meatValue;
    private int decayTimer;

    public Carcass(int meatValue, int decayTimer) {
        this.meatValue = meatValue;
        this.decayTimer = decayTimer;
    }

    @Override
    public void act(World world) {
        if (decayTimer <= 0 || meatValue <= 0) {
            world.delete(this);
        }
    }
}
