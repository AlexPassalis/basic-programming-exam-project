import java.util.Random;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.util.ArrayList;
import itumulator.world.Location;
import java.util.List;
import java.util.Set;


 public class Grass implements Actor {
//
    @Override
    public void act(World world) {
//
   }
////
////
////        // 1.1b Græs kan sprede sig
////
////        Set<Location> neighbours = world.getEmptySurroundingTiles();
////
////        Random random = new Random();
////
////        List<Location> list = new ArrayList<>(neighbours);
////        if (list.isEmpty()) {
////            return;
////        }
////
////        int rand = random.nextInt(list.size());
////        Location l = list.get(rand);
////        world.move(this, l);
//
//
//
//
//
//
//    //getnonblocking til k 1-1c
//
//        // public Object getNonBlocking(Location location) {
//            // validateCoordinates(location);
//            // Object o = tiles[location.getX()][location.getY()][0];
//            // return o;
//        // }
//    }
}
