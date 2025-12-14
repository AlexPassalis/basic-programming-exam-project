package app.animal;

import app.Carcass;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public class Animal implements Actor {
    World world;
    protected double energy;
    private boolean carcass_has_fungi;

    public Animal(World world, boolean carcass_has_fungi) {
        this.world = world;
        this.energy = 100;
        this.carcass_has_fungi = carcass_has_fungi;
    }

    @Override
    public void act(World world) {
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

    protected void eat() {}

    protected int calculateManhattanDistance(Location from, Location to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return dx + dy;
    }

    public void die() {
        Location death_location = world.getLocation(this);
        world.delete(this);
        Carcass animal_carcass = new Carcass(carcass_has_fungi);
        world.setTile(death_location, animal_carcass);
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
