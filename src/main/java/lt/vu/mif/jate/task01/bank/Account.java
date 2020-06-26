package lt.vu.mif.jate.task01.bank;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс банковского счёта.
 */
public class Account {
    /**
     * Международный номер.
     */
    private final String iban;
    /**
     * Банк.
     */
    private Bank bank;
    /**
     * Номер счёта.
     */
    private BigInteger number;
    /**
     * Количество денег в разных валютах.
     */
    private Map<Currency, BigDecimal> balance;

    /**
     * @param pIban международный номер счёта.
     */
    public Account(final String pIban) {
        balance = new HashMap<>();
        Converter.getInstance().getCurrencies().forEach(currency ->
                balance.put(currency, new BigDecimal("0.00")));
        iban = pIban;
    }

    /**
     * @param pIban   международный номер счёта.
     * @param pBank   банк счёта.
     * @param pNumber номер счёта в банке.
     */
    public Account(final String pIban,
                   final Bank pBank,
                   final BigInteger pNumber) {
        this(pIban);
        bank = pBank;
        number = pNumber;
    }

    /**
     * @return банк счёта.
     */
    public final Bank getBank() {
        return bank;
    }

    /**
     * @return номер счёта в банке.
     */
    public final BigInteger getNumber() {
        return number;
    }

    /**
     * @return количество денег в разных валютах.
     */
    public final Map<Currency, BigDecimal> getBalance() {
        return balance;
    }

    @Override
    public final String toString() {
        return iban;
    }

    /**
     * @param currencyCode код валюты.
     * @return количество денег в валюте код которой был передан.
     */
    public final BigDecimal balance(final String currencyCode) {
        return getBalance().get(Currency.getInstance(currencyCode));
    }

    /**
     * @param currencyCode код валюты.
     * @return общее количество денег сконвертированной в валюту, код которой
     * был передан.
     */
    public final BigDecimal balanceAll(final String currencyCode) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<Currency, BigDecimal> entry : balance.entrySet()) {
            sum = sum.add(Converter.getInstance()
                    .convert(entry.getValue(), entry.getKey(),
                            Currency.getInstance(currencyCode)));
        }
        return sum;
    }

    /**
     * Положить деньги на счёт.
     *
     * @param amount       сумма.
     * @param currencyCode код валюты.
     */
    public void credit(final BigDecimal amount,
                       final String currencyCode) {
    }

    /**
     * Положить деньги на счёт в переданной валюте.
     *
     * @param amount   сумма.
     * @param currency валюта.
     */
    public void credit(final BigDecimal amount,
                       final Currency currency) {
    }

    /**
     * Снять деньги со счёта.
     *
     * @param amount       сумма.
     * @param currencyCode код валюты.
     */
    public void debit(final BigDecimal amount,
                      final String currencyCode) {
    }

    /**
     * Снять деньги со счёта в переданной валюте.
     *
     * @param amount   сумма.
     * @param currency валюта.
     */
    public void debit(final BigDecimal amount,
                      final Currency currency) {
    }

    /**
     * Перевести деньги в валюте код которой был передан со счёта на счёт.
     *
     * @param amount       сумма.
     * @param currencyCode код валюты.
     * @param account      счёт.
     */
    public final void debit(final BigDecimal amount,
                            final String currencyCode,
                            final Account account) {
        account.credit(amount, currencyCode);
        debit(amount, currencyCode);
    }

    /**
     * Сконвертировать деньги в другую валюту.
     *
     * @param amount           сумма.
     * @param fromCurrencyCode код валюты из которой выполняется конвертация.
     * @param toCurrencyCode   код валюты в которую выполняется конвертация.
     */
    public final void convert(final BigDecimal amount,
                              final String fromCurrencyCode,
                              final String toCurrencyCode) {
        Currency fromCurrency = Currency.getInstance(fromCurrencyCode);
        debit(Converter.getInstance().checkRange(amount), fromCurrency);
        Currency toCurrency = Currency.getInstance(toCurrencyCode);
        credit(Converter.getInstance()
                .convert(Converter.getInstance().checkRange(amount),
                        fromCurrency, toCurrency), toCurrency);
    }
}
