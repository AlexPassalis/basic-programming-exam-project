import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.world.NonBlocking;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Den implements NonBlocking {
//    private List<Wolf> pack = new ArrayList<>();
//    private int stepCounter = 0;
//    private boolean wolvesInside = false;
//
//    public void addWolf(Wolf wolf) {
//        if (!pack.contains(wolf)) {
//            pack.add(wolf);
//        }
//    }
//
//    @Override
//    public void act(World world) {
//        stepCounter++;
//
//        if (stepCounter % 10 == 0 && !wolvesInside && !pack.isEmpty()){
//            Random random = new Random();
//            int chance = random.nextInt(100);
//            if (chance < 10) {
//                sendWolvesInside(world);
//                createBabyWolf(world);
//                releaseWolves(world);
//            }
//        }
//    }
//
//    private void sendWolvesInside(World world) {
//        for (Wolf wolf : pack) {
//            if (world.contains(wolf)) {
//                world.remove(wolf);
//            }
//        }
//        wolvesInside = true;
//    }
//
//    private void releaseWolves(World world) {
//        Location denLocation = world.getLocation(this);
//        for (Wolf wolf:pack) {
//            if (!world.contains(wolf)) {
//                world.setTile(denLocation ,wolf);
//            }
//        }
//        wolvesInside = false;
//    }
//
//    private void createBabyWolf(World world) {
//        Wolf alpha = getAlpha();
//        if (alpha == null) return;
//        Location denLocation = world.getLocation(this);
//
//        Wolf pup = new Wolf(world, this, alpha);
//        alpha.addFollower(pup);
//        pack.add(pup);
//
//        world.setTile(denLocation, pup);
//    }
//
//    private Wolf getAlpha() {
//        for (Wolf wolf : pack) {
//            if (wolf.isAlpha()) return wolf;
//        }
//        return null;
//    }
}
