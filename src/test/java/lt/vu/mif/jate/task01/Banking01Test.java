package lt.vu.mif.jate.task01;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import lt.vu.mif.jate.task01.bank.Bank;
import static org.junit.Assert.assertNotEquals;


/**
 * Create Bank class (Immutable Bean) that would test.
 * @author valdo
 */
@RunWith(JUnit4.class)
public class Banking01Test {
    
    @Test
    public void bankTest() {
        
        // Test full-fledged constructor and eventual properties
        Bank bank1 = new Bank("LT", 70440, "CBVILT2XXXX", "AB SEB bankas", "Gedimino Ave. 12, LT-01103 Vilnius");
        Locale locale = bank1.getLocale();
        assertEquals("LT", locale.getCountry());
        assertEquals("Lithuania", locale.getDisplayCountry());
        assertEquals((Integer) 70440, bank1.getCode());
        assertEquals("CBVILT2XXXX", bank1.getBicCode());
        assertEquals("AB SEB bankas", bank1.getName());
        assertEquals("Gedimino Ave. 12, LT-01103 Vilnius", bank1.getAddress());

        // Test full-fledged constructor and eventual properties
        Bank bank2 = new Bank("LT", 40100, "AGBLLT2XXXX", "AB DNB bankas", "J.Basanaviciaus Str. 26, LT-03601 Vilnius");
        assertEquals("LT", bank2.getLocale().getCountry());
        assertEquals("Lithuania", bank2.getLocale().getDisplayCountry());
        assertEquals((Integer) 40100, bank2.getCode());
        assertEquals("AGBLLT2XXXX", bank2.getBicCode());
        assertEquals("AB DNB bankas", bank2.getName());
        assertEquals("J.Basanaviciaus Str. 26, LT-03601 Vilnius", bank2.getAddress());

        // Test overloaded constructor and eventual properties
        Bank bank3 = new Bank("LT", 40100, "AGBLLT2XXXX", "AB DNB bankas");
        assertEquals("LT", bank3.getLocale().getCountry());
        assertEquals("Lithuania", bank3.getLocale().getDisplayCountry());
        assertEquals((Integer) 40100, bank3.getCode());
        assertEquals("AGBLLT2XXXX", bank3.getBicCode());
        assertEquals("AB DNB bankas", bank3.getName());
        assertNull(bank3.getAddress());

        // Test overloaded constructor and eventual properties
        Bank bank4 = new Bank("LT", 40100, "AGBLLT2XXXX");
        assertEquals("LT", bank4.getLocale().getCountry());
        assertEquals("Lithuania", bank4.getLocale().getDisplayCountry());
        assertEquals((Integer) 40100, bank4.getCode());
        assertEquals("AGBLLT2XXXX", bank4.getBicCode());
        assertNull(bank4.getName());
        assertNull(bank4.getAddress());
        
        // Test overloaded constructor and eventual properties
        Bank bank5 = new Bank("LT", 40100);
        assertEquals("LT", bank5.getLocale().getCountry());
        assertEquals("Lithuania", bank5.getLocale().getDisplayCountry());
        assertEquals((Integer) 40100, bank5.getCode());
        assertNull(bank5.getBicCode());
        assertNull(bank5.getName());
        assertNull(bank5.getAddress());
        
        // Test overloaded constructor and eventual properties
        Bank bank6 = new Bank("NO", 40100);
        assertEquals("NO", bank6.getLocale().getCountry());
        assertEquals("Norway", bank6.getLocale().getDisplayCountry());
        assertEquals((Integer) 40100, bank6.getCode());
        assertNull(bank6.getBicCode());
        assertNull(bank6.getName());
        assertNull(bank6.getAddress());

        // Test overrided Object method: equals
        assertNotEquals(bank1, bank2);
        assertNotEquals(bank1, bank3);
        assertNotEquals(bank1, bank4);
        assertNotEquals(bank1, bank5);
        assertNotEquals(bank1, bank6);
        assertNotEquals(bank5, bank6);
        
        assertEquals(bank2, bank3);
        assertEquals(bank2, bank4);
        assertEquals(bank2, bank5);
        assertEquals(bank3, bank4);
        assertEquals(bank3, bank5);
        assertEquals(bank4, bank5);

        // Test overrided Object method: hashcode
        Set<Bank> banks = new HashSet<>();
        
        banks.add(bank1);
        assertTrue(1 == banks.size());
        
        banks.add(bank2);
        assertTrue(2 == banks.size());

        banks.add(bank3);
        assertTrue(2 == banks.size());

        banks.add(bank4);
        assertTrue(2 == banks.size());

        banks.add(bank5);
        assertTrue(2 == banks.size());

        // Test overrided Object method: toString
        assertEquals("AB SEB bankas", bank1.toString());
        assertEquals("AB DNB bankas", bank2.toString());
        assertEquals("AB DNB bankas", bank3.toString());
        assertEquals("Bank#40100 (AGBLLT2XXXX), Lithuania", bank4.toString());
        assertEquals("Bank#40100, Lithuania", bank5.toString());
                
    }

}
