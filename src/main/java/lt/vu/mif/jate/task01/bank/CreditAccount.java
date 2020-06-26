package lt.vu.mif.jate.task01.bank;

import lt.vu.mif.jate.task01.bank.exception.AccountActionException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

/**
 * Класс кредитного счета.
 */
public class CreditAccount extends Account {
    /**
     * Брался-ли кредит.
     */
    private boolean isCredit;

    /**
     * @param iban международный код.
     * @param bank банк.
     * @param number номер.
     */
    public CreditAccount(final String iban,
                         final Bank bank,
                         final BigInteger number) {
        super(iban, bank, number);
        isCredit = false;
    }

    @Override
    public final void credit(final BigDecimal amount,
                             final String currencyCode) {
        if (isCredit) {
            throw new AccountActionException();
        }
        isCredit = true;
        credit(Converter.getInstance().checkRange(amount),
                Currency.getInstance(currencyCode));
    }

    @Override
    public final void credit(final BigDecimal amount,
                             final Currency currency) {
        getBalance().put(currency,
                getBalance().get(currency).add(amount));
    }

    @Override
    public final void debit(final BigDecimal amount,
                            final String currencyCode) {
        debit(Converter.getInstance().checkRange(amount),
                Currency.getInstance(currencyCode));
    }

    @Override
    public final void debit(final BigDecimal amount,
                            final Currency currency) {
        getBalance().put(currency,
                getBalance().get(currency).subtract(amount));
    }
}
