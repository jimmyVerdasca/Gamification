package effortMeasurer;

import Program.Movement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jimmy
 */
public class IMUCycleEffortCalculatorTest {
    
    private IMUCycleEffortCalculator ec;
    
    public IMUCycleEffortCalculatorTest() 
            throws IOException, 
            FileNotFoundException, 
            ParseException {
        ec = new IMUCycleEffortCalculator(Movement.RUNNING);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class IMUCycleEffortCalculator.
     */
    @Test
    public void testMappingFunction()
            throws IllegalAccessException, 
            IllegalArgumentException,
            NoSuchMethodException,
            InvocationTargetException {
        System.out.println("testing mapping function");
        Method method = IMUCycleEffortCalculator.class.getDeclaredMethod("mappingFunction", double.class);
        method.setAccessible(true);
        double one = (double) method.invoke(ec, ec.getCurrentFreqTargetted());
        assertEquals(ec.getCurrentFreqTargetted(), one, 0.0000000000001);
        
        double zero = (double) method.invoke(ec, 0);
        assertEquals(0, zero, 0.0000000000001);
        
        double max = (double) method.invoke(ec, ec.getMAX_REACHED());
        assertEquals(ec.getMAX_REACHED(), max, 0.0000000000001);
        
        double valueMidUp = ec.getCurrentFreqTargetted() + 
                0.5 * (ec.getMAX_REACHED() - ec.getCurrentFreqTargetted());
        double middleUp = (double) method.invoke(ec, valueMidUp);
        assertTrue(middleUp >= ec.getCurrentFreqTargetted() && middleUp <= ec.getMAX_REACHED());
        
        double middleDown = (double) method.invoke(ec, ec.getCurrentFreqTargetted() * 0.5);
        assertTrue(middleDown <= ec.getCurrentFreqTargetted() && middleDown >= 0);
        
    }
    
}
