package app;

import app.animal.*;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Carcass implements Actor, NonBlocking, Edible {
    private int meat_amount;
    private Fungi fungi;

    public Carcass(boolean carcass_has_fungi) {
        this.meat_amount = 10;
        if (carcass_has_fungi) {
            fungi = new Fungi(meat_amount);
        }
    }

    public Carcass(boolean carcass_has_fungi, Animal animal) {
        int meat_amount;

        if (animal instanceof Rabbit) {
            meat_amount = 10;
        } else if (animal instanceof Wolf) {
            meat_amount = 20;
        } else if (animal instanceof Deer) {
            meat_amount = 25;
        } else if (animal instanceof Bear) {
            meat_amount = 30;
        } else {
            throw new IllegalArgumentException("Invalid animal type: " + animal.getClass().getSimpleName());
        }

        this.meat_amount = meat_amount;

        if (carcass_has_fungi) {
            fungi = new Fungi(meat_amount);
        }
    }

    @Override
    public void act(World world) {
        if (meat_amount <= 0) {
            Location death_location = world.getLocation(this);
            world.delete(this);
            if (this.fungi != null) {
                this.fungi = new Fungi(meat_amount);

            }
            world.setTile(death_location, fungi);
            return;
        }

        int meat_loss = 1;
        if (hasFungi()) {
            int additional_meat_loss = 1;
            meat_loss = meat_loss + additional_meat_loss;
        }
        meat_amount = meat_amount - meat_loss;
    }

    public boolean hasFungi() {
        return fungi != null;
    }

    public int eatMeat(int meat_amount) {
        if (meat_amount <= 0) {
            return 0;
        }

        int eaten = Math.min(meat_amount, this.meat_amount);
        this.meat_amount = this.meat_amount - eaten;

        return eaten;
    }

    public int getMeatAmount() {
        return meat_amount;
    }

    public void getInfectedByFungi() {
        if (fungi == null) {
            fungi = new Fungi(meat_amount);
        }
    }
}
