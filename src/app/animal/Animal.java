package app.animal;

import app.*;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

/**
 * The Animal class is the base class for all animals in the simulation.
 * It contains shared behavior such as movement, energy handling,
 * eating different food types and dying.
 */
public class Animal implements Actor, Mortal {
    public World world;
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


    /**
     * Defines how the animal decides where to move.
     * This method is intentionally empty in the Animal class and
     * should be overridden by subclasses to implement specific
     * movement behavior.
     */
    protected void movementLogic() {}


    /**
     * Reduces the animal's energy after it has moved.
     * The exact energy loss depends on the animal type
     * and is implemented in subclasses.
     */
    protected void loseEnergyForMoving() {}

    /**
     * Handles a single movement step for the animal.
     * Calls movementLogic() to decide where to move
     * and then loseEnergyForMoving() to reduce energy.
     */
    private void move() {
        movementLogic();
        loseEnergyForMoving();
    }

    /**
     * Calculates the Manhattan distance between two locations.
     * Manhattan distance is the sum of the horizontal and vertical
     * distances between two points.
     * @param from the starting location
     * @param to the target location
     * @return the Manhattan distance between the two locations
     */
    protected int calculateManhattanDistance(Location from, Location to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return dx + dy;
    }

    /**
     * Removes the animal from the world and replaces it with a carcass.
     * The carcass may contain fungi depending on how the animal was created.
     */
    public void die() {
        Location death_location = world.getLocation(this);
        if (world.isOnTile(this)) {
            world.delete(this);
        }
        Carcass animal_carcass = new Carcass(carcass_has_fungi);
        if (!world.containsNonBlocking(death_location)) {
            world.setTile(death_location, animal_carcass);
        }
    }

    /**
     * Eats a grass tile and increases the animal's energy
     * @param grass the grass tile to eat
     */
    public void eatGrass(Grass grass) {
        grass.getEaten(world);
        energy = energy + 15;
    }

    public void eatBerries(Berry berry) {
        if (!berry.hasBerries()) {
            return;
        }
        berry.getEaten(world);
        energy = energy + 20;
    }

    public void eatCarcass(Carcass carcass, Animal animal) {
        double meat_available = carcass.getMeatAmount();
        if (meat_available <= 0) {
            return;
        }

        int meat_amount = 0;
        if (animal instanceof Deer) {
            meat_amount = 2;
        } else if (animal instanceof Wolf) {
            meat_amount = 4;
        } else if (animal instanceof Bear) {
            meat_amount = 10;
        }

        int meat_consumed = carcass.eatMeat(meat_amount);
        if (meat_consumed > 0) {
            double energy_per_meat_consumed = 2;
            energy = energy + (meat_consumed * energy_per_meat_consumed);
        }
    }

    public void eatFungi(Fungi fungi) {
        fungi.getEaten(world);
        energy = energy + 30;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
