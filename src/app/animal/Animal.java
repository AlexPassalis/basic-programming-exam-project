package app.animal;

import app.*;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

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

    protected void movementLogic() {}

    protected void loseEnergyForMoving() {}

    private void move() {
        movementLogic();
        loseEnergyForMoving();
    }

    public static void eat() {}

    protected int calculateManhattanDistance(Location from, Location to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return dx + dy;
    }

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
