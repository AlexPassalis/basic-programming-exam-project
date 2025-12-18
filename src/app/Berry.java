package app;

import java.awt.Color;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DynamicDisplayInformationProvider;

public class Berry implements Actor, NonBlocking, Edible, DynamicDisplayInformationProvider {
    private int berries;
    private int simulation_counts;

    public Berry() {
        berries = 0;
        simulation_counts = 0;
    }

    @Override
    public void act (World world) {
        simulation_counts = simulation_counts + 1;

        if (simulation_counts % 10 == 0) {
            berries = berries + 1;
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (berries > 0) {
            return new DisplayInformation(Color.green, "custom-bush-berries");
        } else {
            return new DisplayInformation(Color.yellow, "custom-bush");
        }
    }

    public boolean hasBerries() {
        return berries > 0;
    }

    public int getBerries() {
        return berries;
    }

    @Override
    public void getEaten(World world) {
        berries = 0;
    }
}
