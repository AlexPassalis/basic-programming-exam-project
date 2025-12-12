package app;

import app.animal.Animal;
import app.animal.Bear;
import app.animal.Rabbit;
import app.animal.Wolf;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Carcass implements Actor, NonBlocking {
    private double meat_amount;
    private Fungi fungi;

    public Carcass(boolean carcass_has_fungi) {
        this.meat_amount = 10;
        if (carcass_has_fungi) {
            fungi = new Fungi();
        }
    }

    public Carcass(boolean carcass_has_fungi, Animal animal) {
        int meat_amount;

        if (animal instanceof Rabbit) {
            meat_amount = 10;
        } else if (animal instanceof Wolf) {
            meat_amount = 20;
        } else if (animal instanceof Bear) { // TODO the Bear never dies, add the Dear here instead
            meat_amount = 20;
        } else {
            throw new IllegalArgumentException("Invalid animal type: " + animal.getClass().getSimpleName());
        }

        this.meat_amount = meat_amount;

        if (carcass_has_fungi) {
            fungi = new Fungi();
        }
    }

    @Override
    public void act(World world) {
        if (meat_amount <= 0) {
            Location death_location = world.getLocation(this);
            world.delete(this);
            if (this.fungi != null) {
                Fungi carcass_fungi = new Fungi();
                world.setTile(death_location, carcass_fungi);
            }
        }

        double meat_loss = 1;
        if (hasMushroom()) {
            double additional_meat_loss = 0.5;
            meat_loss = meat_loss + additional_meat_loss;
        }
        meat_amount = meat_amount - meat_loss;
    }

    private boolean hasMushroom() {
        return fungi != null;
    }
}
