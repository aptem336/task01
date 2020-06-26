package lt.vu.mif.jate.task01.bank;

import lt.vu.mif.jate.task01.bank.exception.NoFundsException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

/**
 * Класс лицевого счёта.
 */
public class CurrentAccount extends Account {
    /**
     * @param iban международный код.
     * @param bank банк.
     * @param number номер.
     */
    public CurrentAccount(final String iban,
                          final Bank bank,
                          final BigInteger number) {
        super(iban, bank, number);
    }

    @Override
    public final void credit(final BigDecimal amount,
                             final String currencyCode) {
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
        if (getBalance().get(currency).compareTo(amount) < 0) {
            throw new NoFundsException();
        }
        getBalance().put(currency,
                getBalance().get(currency).subtract(amount));
    }
}
