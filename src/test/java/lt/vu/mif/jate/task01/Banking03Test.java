package lt.vu.mif.jate.task01;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import lt.vu.mif.jate.task01.bank.Account;
import lt.vu.mif.jate.task01.bank.CurrentAccount;
import lt.vu.mif.jate.task01.bank.SavingsAccount;
import lt.vu.mif.jate.task01.bank.CreditAccount;
import lt.vu.mif.jate.task01.bank.Banking;
import lt.vu.mif.jate.task01.bank.exception.BankNotFoundException;
import lt.vu.mif.jate.task01.bank.exception.IBANException;
import lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException;
import static org.junit.Assert.assertNotEquals;

/**
 * Create Account classes that would pass tests.
 * IBAN numbers should be checked by patterns loaded from resource 
 * file /banking/iban.txt. This file contains lines for IBAN numbers
 * per country, i.e.
 * Costa Rica:CRkk bbbc cccc cccc cccc c
 * where: 
 *  - Costa Rica - country name
 *  - CRkk bbbc cccc cccc cccc c - IBAN number pattern, where
 *  - CR - country code
 *  - bbb - bank code
 *  - c cccc cccc cccc c - account number
 *  - kk, xx, etc. are just numbers, not important
 * @author valdo
 */
@RunWith(JUnit4.class)
public class Banking03Test {
    
    /**
     * Banking class should construct Account instances based on provided
     * IBAN number. Account class should have specific properties set.
     * IBAN number should be checked based on country and pattern loaded
     * from the resource.
     * @throws BankNotFoundException
     * @throws IBANException
     * @throws WrongAccountTypeException 
     */
    @Test
    public void accountTest() throws BankNotFoundException, IBANException, WrongAccountTypeException {
        
        // Singleton instance
        Banking banking = Banking.getInstance();
        
        // Create account and test
        Account a1 = banking.getCurrentAccount("Lt 337 30001 0077 2111 11");
        assertSame(banking.getBank("LT", 73000), a1.getBank());
        assertEquals("LT", a1.getBank().getLocale().getCountry());
        assertEquals(new BigInteger("10077211111"), a1.getNumber());
        assertEquals("LT337300010077211111", new StringBuffer().append(a1).toString());

        // Create account and test. Bank should be created on the fly!
        Account a2 = banking.getCurrentAccount("NO12 252512 11567");
        assertSame(banking.getBank("NO", 2525), a2.getBank());
        assertEquals("NO", a2.getBank().getLocale().getCountry());
        assertEquals(new BigInteger("121156"), a2.getNumber());
        assertEquals("NO1225251211567", a2.toString());
        
        // Create account and test. Bank should be created on the fly!
        Account a3 = banking.getCreditAccount("qa9998761111 2222333344 4455556");
        assertSame(banking.getBank("QA", 9876), a3.getBank());
        assertEquals("QA", a3.getBank().getLocale().getCountry());
        assertEquals(new BigInteger("111122223333444455556"), a3.getNumber());
        assertEquals("QA999876111122223333444455556", a3.toString());

        // Check wrong account number for country!
        try {
            banking.getCurrentAccount("LL331234010077211111");
            fail("Account country not checked");
        } catch (IBANException ex) {
            assertEquals("LL331234010077211111", ex.getValue());
            assertEquals("IBAN country not found: LL", ex.getMessage());
        }
        
        // Check wrong account number for country!
        try {
            banking.getCurrentAccount("LT33123401007721111");
            fail("Account length is not checked");
        } catch (IBANException ex) {
            assertEquals("LT33123401007721111", ex.getValue());
            assertEquals("IBAN number length wrong: expected 20, got 19", ex.getMessage());
        }
        
        // Check wrong account number for country!
        try {
            banking.getSavingsAccount("CH1234511231458485268899");
            fail("Account length is not checked");
        } catch (IBANException ex) {
            assertEquals("CH1234511231458485268899", ex.getValue());
            assertEquals("IBAN number length wrong: expected 21, got 24", ex.getMessage());
        }

        // Check wrong account number for country!
        try {
            banking.getCurrentAccount("NO1225251234S77");
            fail("Account format is not checked");
        } catch (IBANException ex) {
            assertEquals("NO1225251234S77", ex.getValue());
            assertEquals("IBAN format wrong: NO1225251234S77", ex.getMessage());
        }

        // Check wrong account number for country!
        try {
            banking.getCreditAccount("LV12 1525 4521 5525 9555 A");
            fail("Account format is not checked");
        } catch (IBANException ex) {
            assertEquals("LV121525452155259555A", ex.getValue());
            assertEquals("IBAN format wrong: LV121525452155259555A", ex.getMessage());
        }

        // Check wrong account number for country!
        try {
            banking.getCurrentAccount("LW1215254521552595551");
            fail("Account format is not checked");
        } catch (IBANException ex) {
            assertEquals("LW1215254521552595551", ex.getValue());
            assertEquals("IBAN country not found: LW", ex.getMessage());
        }

        // Create account and test. Bank should be created on the fly!
        Account a4 = banking.getCurrentAccount("FI12 1234 5612 3456 78");
        assertEquals((Integer) 123456, a4.getBank().getCode());
        assertEquals("FI", a4.getBank().getLocale().getCountry());
        assertEquals("Finland", a4.getBank().getLocale().getDisplayCountry());
        assertEquals(new BigInteger("1234567"), a4.getNumber());
        assertEquals("FI1212345612345678", new StringBuffer().append(a4).toString());

        // Once created, same account should be returned!
        Account a5 = banking.getCurrentAccount("fi 1212345612345678");
        assertSame(a4.getBank(), a5.getBank());
        assertEquals(a4.getBank().getLocale(), a5.getBank().getLocale());
        
        // Checking that account is the same account!
        assertNotEquals(a1, a4);
        assertNotEquals(a1, a5);
        assertNotEquals(a2, a5);
        assertEquals(a4, a5);
        assertSame(a4, a5);
        
    }
    
    /**
     * Test different Account types (subclasses)
     * @throws lt.vu.mif.jate.task01.bank.exception.IBANException
     * @throws lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException
     */
    @Test
    public void accoutTypesTest() throws IBANException, WrongAccountTypeException {
        
        // Singleton instance
        Banking banking = Banking.getInstance();
        
        // Account class should be abstract!
        Account a0 = new Account("LT337300010077211111") {
            
            public void credit(BigDecimal ammount, Currency currency) { }
            
            public void debit(BigDecimal ammount, Currency currency) { }
            
            public void debit(BigDecimal ammount, Currency currency, Account creditAccount) { }
            
        };
        
        // Test CurrentAccount subclass
        Account a1 = banking.getCurrentAccount("LT337300010077211111");
        assertTrue(a1 instanceof CurrentAccount);
        
        // Trying to take the wrong type account reference should fail
        try {
            banking.getSavingsAccount("LT337300010077211111");
            fail("Existing account type not checked");
        } catch (WrongAccountTypeException ex) {
            assertEquals("Account type was CurrentAccount", ex.getMessage());
        }

        try {
            banking.getCreditAccount("LT337300010077211111");
            fail("Existing account type not checked");
        } catch (WrongAccountTypeException ex) {
            assertEquals("Account type was CurrentAccount", ex.getMessage());
        }
        
        Account a2 = banking.getSavingsAccount("LT337300010077222112");
        assertTrue(a2 instanceof SavingsAccount);
        
        try {
            banking.getCurrentAccount("LT337300010077222112");
            fail("Existing account type not checked");
        } catch (WrongAccountTypeException ex) {
            assertEquals("Account type was SavingsAccount", ex.getMessage());
        }

        try {
            banking.getCreditAccount("LT337300010077222112");
            fail("Existing account type not checked");
        } catch (WrongAccountTypeException ex) {
            assertEquals("Account type was SavingsAccount", ex.getMessage());
        }
        
        Account a3 = banking.getCreditAccount("LT337300010077211113");
        assertTrue(a3 instanceof CreditAccount);
        
        try {
            banking.getSavingsAccount("LT337300010077211113");
            fail("Existing account type not checked");
        } catch (WrongAccountTypeException ex) {
            assertEquals("Account type was CreditAccount", ex.getMessage());
        }

        try {
            banking.getCurrentAccount("LT337300010077211113");
            fail("Existing account type not checked");
        } catch (WrongAccountTypeException ex) {
            assertEquals("Account type was CreditAccount", ex.getMessage());
        }
        
    }
    
}
