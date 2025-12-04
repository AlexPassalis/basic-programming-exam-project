package app;

import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Carcass implements Actor, NonBlocking {
    private int meatValue;
    private int decayTimer;
    private boolean hasMushroom;

    public Carcass(int meatValue, int decayTimer, boolean hasMushroom) {
        this.meatValue = meatValue;
        this.decayTimer = decayTimer;
        this.hasMushroom = hasMushroom;
    }

    @Override
    public void act(World world) {

        if (decayTimer <= 0 || meatValue <= 0) {
            world.delete(this);

            
        }
    }
}
