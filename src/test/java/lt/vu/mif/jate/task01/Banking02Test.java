package lt.vu.mif.jate.task01;

import java.util.Map;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import lt.vu.mif.jate.task01.bank.Bank;
import lt.vu.mif.jate.task01.bank.Banking;
import lt.vu.mif.jate.task01.bank.exception.BankNotFoundException;

/**
 * Create Banking class (Singleton Fascade pattern) that would pass tests.
 * List of initial banks should be loaded from resource: /banking/banks.txt
 * @author valdo
 */
@RunWith(JUnit4.class)
public class Banking02Test {

    @Test
    public void bankingBankTest() {
        
        // Banking is Singleton
        Banking banking = Banking.getInstance();
        assertSame(banking, Banking.getInstance());
        
        Bank bank1 = null, bank2 = null, bank3 = null;
        
        // Test if banks have been correctly loaded from resource?
        try {
        
            bank1 = banking.getBank("LT", 70440);
            assertEquals((Integer) 70440, bank1.getCode());
            assertEquals("CBVILT2XXXX", bank1.getBicCode());
            assertEquals("AB SEB bankas", bank1.getName());
            assertEquals("Gedimino Ave. 12, LT-01103 Vilnius", bank1.getAddress());

            bank2 = banking.getBank("LT", 40100);
            assertEquals((Integer) 40100, bank2.getCode());
            assertEquals("AGBLLT2XXXX", bank2.getBicCode());
            assertEquals("AB DNB bankas", bank2.getName());
            assertEquals("J.Basanaviciaus Str. 26, LT-03601 Vilnius", bank2.getAddress());

            bank3 = banking.getBank("LT", 72900);
            assertEquals((Integer) 72900, bank3.getCode());
            assertEquals("INDULT2XXXX", bank3.getBicCode());
            assertEquals("Citadele Bank", bank3.getName());
            assertEquals("K.Kalinausko Str. 13, LT-03107 Vilnius", bank3.getAddress());
                
        } catch (BankNotFoundException ex) {
            fail("These banks should have been found...");
        }

        // Test that other banks are not present and the
        // correct exception is generated
        try {
            
            banking.getBank("FI", 72900);
            fail("Bank existence is not checked correctly");
            
        } catch (BankNotFoundException ex) {
            assertEquals("Bank (FI-72900) was not found.", ex.getMessage());
            assertEquals("FI", ex.getCountry());
            assertEquals((Integer) 72900, ex.getCode());
        }
        
        try {
            
            banking.getBank("LT", 12345);
            fail("Bank existence is not checked correctly");
            
        } catch (BankNotFoundException ex) {
            assertEquals("Bank (LT-12345) was not found.", ex.getMessage());
            assertEquals("LT", ex.getCountry());
            assertEquals((Integer) 12345, ex.getCode());
        }

        // Test getBanks method and that returned
        // list modification does not alter the source
        
        Map<String, Map<Integer, Bank>> banks = banking.getBanks();
        
        assertTrue(banks.get("LT").size() == 6);
        
        assertTrue(banks.get("LT").values().contains(bank1));
        assertTrue(banks.get("LT").values().contains(bank2));
        assertTrue(banks.get("LT").values().contains(bank3));
        
        banks.get("LT").values().remove(bank1);
        banks.get("LT").values().remove(bank2);
        
        banks.clear();
        assertTrue(banks.isEmpty());
        assertEquals(6, banking.getBanks().get("LT").size());

    }
    
}
