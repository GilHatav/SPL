package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class InventoryTest {

    private  Inventory inventory;
    private String [] gadgets;
    @BeforeEach
    public void setUp(){
        inventory = Inventory.getInstance();
        inventory.load(gadgets);
    }

    @Test
    public void test_getItem1(){
        String g = gadgets[0];
        boolean output = inventory.getItem(g);
        assertEquals(output,true);
    }

    @Test
    public void test_getItem2(){
        String g = gadgets[0];
        inventory.getItem(g);
        boolean output = inventory.getItem(g);

        assertEquals(output,false);

    }
}