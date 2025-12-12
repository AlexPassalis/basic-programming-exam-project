package app.animal;

public class Predator extends Animal {
    public Predator(boolean carcass_has_fungi) {
        super(carcass_has_fungi);
    }

    public void kill(Animal animal) {
        animal.die();
    }
}
