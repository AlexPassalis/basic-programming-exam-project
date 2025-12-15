package Test;
import app.*;
import app.animal.Rabbit;
import app.animal.Wolf;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import app.Carcass;
import itumulator.world.*;

import java.io.FileNotFoundException;

public class TestCarcass extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Carcass(false));
    }

    @Test
    public void animal_becomes_carcass_and_can_be_eaten() throws FileNotFoundException {
          setUp();

          Location rabbitLocation = new Location(4,4);
          Rabbit rabbit = new Rabbit(world, false);
          world.setTile(rabbitLocation, rabbit);

          rabbit.die();
          Object nonBlocking = world.getNonBlocking(rabbitLocation);
          assertTrue(nonBlocking instanceof Carcass);

          Carcass carcass = (Carcass) nonBlocking;
          int initialMeat = carcass.getMeatAmount();

          Wolf wolf = new Wolf(world, false, new Den());
          world.setTile(new Location(4, 5), wolf);
          wolf.eatCarcass(carcass, wolf);

          assertTrue(carcass.getMeatAmount() < initialMeat);
      }

      @Test
      public void carcass_can_disappear () throws FileNotFoundException {
          setUp();
          Location location = new Location(3,3);
          Carcass carcass = new Carcass(false);
          world.setTile(location, carcass);

          final int MAX_STEPS = 50;
          for (int i = 0; i < MAX_STEPS; i++) {
              if (!world.containsNonBlocking(location)) {
                  break;
              }
              carcass.act(world);
          }
          Object nonBlocking = world.getNonBlocking(location);
          assertTrue(nonBlocking == null || !(nonBlocking instanceof Carcass));
      }
}
