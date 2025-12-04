package app;

import itumulator.simulator.Actor;
import itumulator.world.World;

public class Animal implements Actor {
    World world;
    protected double energy;

    public Animal() {
        this.energy = 100;
    }

    @Override
    public void act(World world) {
        this.world = world;

        move();

        if (this.energy <= 0){
            die();
        }
    }

    protected void movementLogic() {}

    protected void loseEnergyForMoving() {}

    private void move() {
        movementLogic();
        loseEnergyForMoving();
    }

    private void die() {
        world.delete(this);
    }
}
