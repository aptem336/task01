package lt.vu.mif.jate.task01;

import java.math.BigDecimal;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import lt.vu.mif.jate.task01.bank.Account;
import lt.vu.mif.jate.task01.bank.Banking;
import lt.vu.mif.jate.task01.bank.exception.AccountActionException;
import lt.vu.mif.jate.task01.bank.exception.IBANException;
import lt.vu.mif.jate.task01.bank.exception.NoFundsException;
import lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException;

/**
 * Create Account classes that would test.
 * Each Account can hold many deposits in different currencies.
 * Please implement credit, debit and balance operations.
 * @author valdo
 */
@RunWith(JUnit4.class)
public class Banking05Test implements BankingTestIf {
        
    private final Banking banking = Banking.getInstance();

    /**
     * Cash credit/debit tests
     * @throws lt.vu.mif.jate.task01.bank.exception.IBANException
     * @throws lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException
     * @throws lt.vu.mif.jate.task01.bank.exception.NoFundsException
     */
    @Test
    public void cashTest() throws IBANException, WrongAccountTypeException, NoFundsException, AccountActionException {
        
        Account a1 = banking.getCurrentAccount("LT337300010077211111");
        
        a1.credit(BD("1000.00"), "EUR");
        assertEquals(BD("1000.00"), a1.balance("EUR"));
        assertEquals(BD("0.00"), a1.balance("USD"));
        assertEquals(BD("1116.30"), a1.balanceAll("USD"));
        assertEquals(BD("0.00"), a1.balance("HUF"));
        assertEquals(BD("309602.00"), a1.balanceAll("HUF"));
        
        a1.credit(BD("11.11"), "HUF");
        assertEquals(BD("11.11"), a1.balance("HUF"));
        assertEquals(BD("309613.11"), a1.balanceAll("HUF"));
        assertEquals(BD("1000.00"), a1.balance("EUR"));
        assertEquals(BD("1000.04"), a1.balanceAll("EUR"));
        
        a1.debit(BD("1000.00"), "EUR");
        assertEquals(BD("0.00"), a1.balance("EUR"));
        assertEquals(BD("0.04"), a1.balanceAll("EUR"));
        assertEquals(BD("11.11"), a1.balance("HUF"));

        // Can not debit if funds are not enough
        checkException(BD("1.00"), s -> a1.debit(s, "EUR"), NoFundsException.class);
        
        a1.debit(BD("11.11"), "HUF");
        assertEquals(BD("0.00"), a1.balance("EUR"));
        assertEquals(BD("0.00"), a1.balance("HUF"));
        
        a1.credit(BD("1000000.00"), "RUB");
        assertEquals(BD("0.00"), a1.balance("EUR"));
        assertEquals(BD("11610.00"), a1.balanceAll("EUR"));
        assertEquals(BD("0.00"), a1.balance("USD"));
        assertEquals(BD("12960.24"), a1.balanceAll("USD"));
        assertEquals(BD("1000000.00"), a1.balance("RUB"));

        a1.credit(BD("12960.24"), "USD");
        assertEquals(BD("12960.24"), a1.balance("USD"));
        assertEquals(BD("25920.48"), a1.balanceAll("USD"));
        assertEquals(BD("1000000.00"), a1.balance("RUB"));
        assertEquals(BD("2002183.42"), a1.balanceAll("RUB"));
        
        // Wrong debit values
        checkException("LT331234010077211111", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("one hundred", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("-100", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("100.123", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("1000.1a", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("1,000.00", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("100,10", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        checkException("FF", s -> a1.credit(BD(s), "USD"), NumberFormatException.class);
        
        Account a2 = banking.getCurrentAccount("LT337300010077211111");
        
        assertEquals(BD("12960.24"), a2.balance("USD"));
        assertEquals(BD("25920.48"), a2.balanceAll("USD"));
        assertEquals(BD("1000000.00"), a2.balance("RUB"));
        assertEquals(BD("2002183.42"), a2.balanceAll("RUB"));
        
        a2.debit(BD("10000"), "USD");
        assertEquals(BD("2960.24"), a2.balance("USD"));
        assertEquals(BD("15920.48"), a2.balanceAll("USD"));
        
        // Not enough funds here
        checkException(BD("1.00"), s -> a2.debit(s, "EUR"), NoFundsException.class);
        
    }
    
    /**
     * In account currency convert test.
     * Account currencies can be converted.
     * @throws lt.vu.mif.jate.task01.bank.exception.IBANException
     * @throws lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException
     * @throws lt.vu.mif.jate.task01.bank.exception.NoFundsException
     */
    @Test
    public void convertionTest() throws IBANException, WrongAccountTypeException, NoFundsException, AccountActionException {
        
        Account a1 = banking.getCurrentAccount("LT337300010077211112");
        Account a2 = banking.getCurrentAccount("LT337300010077211112");
        
        assertEquals(BD("0.00"), a1.balance("EUR"));
        assertEquals(BD("0.00"), a2.balance("EUR"));
        
        a1.credit(BD("1000.00"), "USD");

        assertEquals(BD("0.00"), a1.balance("EUR"));
        assertEquals(BD("896.60"), a1.balanceAll("EUR"));
        assertEquals(BD("0.00"), a2.balance("EUR"));
        assertEquals(BD("896.60"), a2.balanceAll("EUR"));

        // No funds
        checkException(BD("100.00"), s -> a2.debit(s, "EUR"), NoFundsException.class);
        
        a1.convert(BD("1000.00"), "USD", "EUR");

        a2.debit(BD("100.00"), "EUR");
        
        assertEquals(BD("796.60"), a1.balance("EUR"));
        assertEquals(BD("796.60"), a2.balance("EUR"));
        
        // No funds
        checkException(BD("1.00"), s -> a2.debit(s, "USD"), NoFundsException.class);
        checkException(BD("1.00"), s -> a2.convert(s, "USD", "EUR"), NoFundsException.class);
        checkException(BD("797.00"), s -> a2.convert(s, "EUR", "BLR"), NoFundsException.class);
        
    }
        
    /**
     * Account credit/debit tests.
     * Funds can be transfered between accounts. With exceptions:
     * - CurrentAccount can be used for any credit/debit operations if funds are
     *   sufficient.
     * - SavingsAccount can not be debited!
     * - CreditAccount can be credited only once after opening!
     * @throws lt.vu.mif.jate.task01.bank.exception.IBANException
     * @throws lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException
     * @throws lt.vu.mif.jate.task01.bank.exception.NoFundsException
     * @throws lt.vu.mif.jate.task01.bank.exception.AccountActionException
     */
    @Test
    public void transferTest() throws IBANException, WrongAccountTypeException, NoFundsException, AccountActionException {

        Account a1 = banking.getCurrentAccount("FI1212345612345678");
        Account a2 = banking.getCurrentAccount("FI1212345612345679");
        Account a3 = banking.getSavingsAccount("NO1225251234567");
        Account a4 = banking.getCreditAccount("QA999876111122223333444455556");

        a1.credit(BD("1000.00"), "EUR");
        
        assertEquals(BD("1000.00"), a1.balance("EUR"));
        assertEquals(BD("0.00"), a2.balance("EUR"));

        a1.debit(BD("100.00"), "EUR", a2);
        
        assertEquals(BD("900.00"), a1.balance("EUR"));
        assertEquals(BD("100.00"), a2.balance("EUR"));
        
        a2.debit(BD("50.00"), "EUR", a1);
        
        assertEquals(BD("950.00"), a1.balance("EUR"));
        assertEquals(BD("50.00"), a2.balance("EUR"));
        
        a3.credit(BigDecimal.TEN, "USD");
        a3.convert(BigDecimal.ONE, "USD", "LTL");
        
        assertEquals(BD("9.00"), a3.balance("USD"));
        assertEquals(BD("10.00"), a3.balanceAll("USD"));
        assertEquals(BD("3.10"), a3.balance("LTL"));
        assertEquals(BD("30.96"), a3.balanceAll("LTL"));
        
        checkException(BigDecimal.ONE, s -> a3.debit(s, "USD"), AccountActionException.class);
        checkException(BigDecimal.ONE, s -> a3.debit(s, "USD", a1), AccountActionException.class);

        assertEquals(BD("0.00"), a4.balance("EUR"));
        
        a4.credit(BD("1000.00"), "EUR");
        checkException(BigDecimal.ONE, s -> a4.credit(s, "EUR"), AccountActionException.class);
        checkException(BigDecimal.ONE, s -> a4.credit(s, "USD"), AccountActionException.class);
        checkException(BigDecimal.ONE, s -> a4.credit(s, "LTL"), AccountActionException.class);
        
        a4.convert(BD("15.00"), "EUR", "USD");
        a4.debit(BigDecimal.ONE, "EUR");
        a4.debit(BigDecimal.ONE, "EUR");
        a4.debit(BigDecimal.ONE, "USD", a3);
        
        assertEquals(BD("983.00"), a4.balance("EUR"));
        assertEquals(BD("15.74"), a4.balance("USD"));
        assertEquals(BD("1113.06"), a4.balanceAll("USD"));
        assertEquals(BD("0.00"), a3.balance("EUR"));
        assertEquals(BD("9.87"), a3.balanceAll("EUR"));
        
        checkException(BigDecimal.ONE, s -> a2.debit(s, "EUR", a4), AccountActionException.class);
        
    }
    
}
