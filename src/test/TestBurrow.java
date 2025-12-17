package test;

import app.*;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;

public class TestBurrow extends TestSuper {
    @Test
    public void gets_initialized() throws FileNotFoundException {
        getsInitialized("src/data/week-1/t1-3a.txt", Burrow.class);
    }
}
