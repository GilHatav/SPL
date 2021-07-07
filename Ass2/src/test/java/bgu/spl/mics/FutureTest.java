package bgu.spl.mics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FutureTest {
    Future f;
    @BeforeEach
    public void setUp(){
        f = new Future();
    }

    @Test
    public void test_result()
    {
        Integer i = 1;
        f.resolve(i);
        boolean expected = f.getClass().isAssignableFrom(Integer.class);
        assertEquals(expected,true);
    }

    @Test
    public void TimerGet1_test()
    {
        try {
            Integer i1 = (Integer)f.get(3000, TimeUnit.MILLISECONDS);
            Thread.sleep(3000);
            assertEquals(i1,null);
        }
        catch (Exception e)
        {
            boolean b = false;
            assertEquals(b,true);
        }
    }
    @Test
    public void TimerGet2_test()
    {
        try {
            Integer i = 1;
            Integer i1 = (Integer)f.get(3000, TimeUnit.MILLISECONDS);
            f.resolve(i);
            Thread.sleep(3000);
            assertEquals(i1,i);
        }
        catch (Exception e)
        {
            boolean b = false;
            assertEquals(b,true);
        }
    }
    @Test
    public void isDone_Test()
    {
        try {
            Integer i = 1;
            f.resolve(i);
            Integer i1 = (Integer) f.get();
            assertEquals(i1,i);
        }
        catch (Exception e)
        {
            boolean b = false;
            assertEquals(b,true);
        }
    }

}