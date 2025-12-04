import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

//public class Mushroom implements Actor, NonBlocking {
    //private int mushroomTimer;


    // Mushroom(int mushroomTimer) {
        //this.mushroomTimer = mushroomTimer; //sæt mushroomtimer til at være samme værdi som meat. "Desto større ådslet er, desto længere vil svampen leve efter ådslet er væk."
    //}

//    @Override
//    public void act(World world) {
//    Location current_location = world.getLocation(this);

//    (Set<Location> surrounding_tiles = world.getSurroundingTiles(current_location, 2);
//    for (Location location : surrounding_tiles) {
//          Object tile = world.getTile(location);
//              if (tile instanceof Mushroom) {
//                  return;
//          else if (tile !instanceof Mushroom) {
//                  mushroomTimer = mushroomTimer - 1; //hvis der ikke er svampe i nærheden, så kan der ikke spredes.
//
//        if (mushroomTimer <= 0) {
//            world.delete(this);
