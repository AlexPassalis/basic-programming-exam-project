package app.animal;

import app.Carcass;
import itumulator.world.World;

public class Predator extends Animal {
    public Predator(World world, boolean carcass_has_fungi) {
        super(world, carcass_has_fungi);
    }

    public void kill(Animal animal) {
        animal.die();
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
            double energy_per_meat_consumed = 2; // 2 energy per meat consumed from the carcass
            energy = energy + (meat_consumed * energy_per_meat_consumed);
        }
    }
}
