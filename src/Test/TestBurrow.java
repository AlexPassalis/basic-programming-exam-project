package test;

import app.*;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;

public class TestBurrow extends TestSuper {
    @Test
    public void gets_initialised() throws FileNotFoundException {
        setUp();
        testInitialization(new Burrow());
    }
}
