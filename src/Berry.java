import java.awt.Color;
import itumulator.executable.Program;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Berry implements Actor, NonBlocking {
    private Program program;
    private int berries;
    private int simulation_counts;

    Berry(Program program) {
        this.program = program;
        this.berries = 0;
    }

    @Override
    public void act (World world) {
        if (simulation_counts > 0 && simulation_counts % 25 == 0) {
            berries = berries + 1;
        }

        if (berries > 0) {
            program.setDisplayInformation(this.getClass(), new DisplayInformation(Color.green , "custom-bush-berries"));
        } else {
            program.setDisplayInformation(this.getClass(), new DisplayInformation(Color.green , "custom-bush"));
        }

        simulation_counts = simulation_counts + 1;
    }

    public boolean hasBerries() {
        return berries > 0;
    }

    public int eatBerries() {
        if (berries > 0) {
            int current_berries = berries;
            this.berries = 0;
            return current_berries;
        }

        return 0;
    }

}
