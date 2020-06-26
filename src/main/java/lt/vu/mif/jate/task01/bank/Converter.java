package lt.vu.mif.jate.task01.bank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static lt.vu.mif.jate.task01.bank.Banking.RESOURCES_PATH;

/**
 * Класс конвертера.
 */
public class Converter {
    /**
     * Режим округдения.
     */
    private static final int SCALE = 2;
    /**
     * Синглтон конвертера.
     */
    private static Converter instance;
    /**
     * Базовая валюта.
     */
    private Currency baseCurrency;
    /**
     * Курсы валют.
     */
    private Map<Currency, Rate> rates;

    {
        baseCurrency = Currency.getInstance("EUR");
        try (BufferedReader ratesBufferedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(RESOURCES_PATH + "rates.txt"),
                        StandardCharsets.UTF_8));) {
            rates = readRates(ratesBufferedReader);
        } catch (IOException ignored) {
        }
    }

    /**
     * @return объект конвертера.
     */
    public static synchronized Converter getInstance() {
        if (instance == null) {
            instance = new Converter();
        }
        return instance;
    }

    /**
     * Метод чтения курсов валют из файла.
     *
     * @param ratesBufferedReader ридер файла.
     * @return курсы валют.
     * @throws IOException ошибка чтения.
     */
    private Map<Currency, Rate> readRates(
            final BufferedReader ratesBufferedReader)
            throws IOException {
        Map<Currency, Rate> newRates = new HashMap<>();
        String rateString;
        while ((rateString = ratesBufferedReader.readLine()) != null) {
            String[] rateAttributes = rateString.split(":");
            String currencyCode = rateAttributes[0];
            try {
                Currency currency = Currency.getInstance(currencyCode);
                BigDecimal to = new BigDecimal(rateAttributes[1]);
                BigDecimal from = new BigDecimal(rateAttributes[2]);
                newRates.put(currency, new Rate(to, from));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return newRates;
    }

    /**
     * @return базовая валюта.
     */
    public final Currency getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * @return валюты.
     */
    public final Set<Currency> getCurrencies() {
        return rates.keySet();
    }

    /**
     * @param currencyCode код валюты.
     * @return курс валюта относительно базовой.
     */
    public final BigDecimal getRateToBase(final String currencyCode) {
        return getRateToBase(Currency.getInstance(currencyCode));
    }

    /**
     * @param currency валюта.
     * @return курс валюта относительно базовой.
     */
    public final BigDecimal getRateToBase(final Currency currency) {
        return rates.get(currency).to;
    }

    /**
     * @param currencyCode код валюты.
     * @return курс базовой относительно валюты.
     */
    public final BigDecimal getRateFromBase(final String currencyCode) {
        return getRateFromBase(Currency.getInstance(currencyCode));
    }

    /**
     * @param currency валюта.
     * @return курс базовой относительно валюты.
     */
    public final BigDecimal getRateFromBase(final Currency currency) {
        return rates.get(currency).from;
    }

    /**
     * Конвертация в базовую валюту.
     * @param amountString сумма.
     * @param currencyCode код валюты.
     * @return сконвертированная сумма.
     */
    public final BigDecimal toBase(final String amountString,
                                   final String currencyCode) {
        return toBase(parseBigDecimal(amountString),
                Currency.getInstance(currencyCode))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Конвертация в базовую валюту.
     * @param amount сумма.
     * @param currency валюты.
     * @return сконвертированная сумма.
     */
    private BigDecimal toBase(final BigDecimal amount,
                              final Currency currency) {
        return getRateToBase(currency).multiply(amount);
    }

    /**
     * Конвертация из базовой валюты.
     * @param amountString сумма.
     * @param currencyCode код валюты.
     * @return сконвертированная сумма.
     */
    public final BigDecimal fromBase(final String amountString,
                                     final String currencyCode) {
        return fromBase(parseBigDecimal(amountString),
                Currency.getInstance(currencyCode))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Конвертация из базовой валюты.
     * @param amount сумма.
     * @param currency валюта.
     * @return сконвертированная сумма.
     */
    private BigDecimal fromBase(final BigDecimal amount,
                                final Currency currency) {
        return getRateFromBase(currency).multiply(amount);
    }

    /**
     * Конвертация из валюты в валюту.
     * @param amountString сумма.
     * @param toCurrencyCode код валюты из которой выполняется конвертация.
     * @param fromCurrencyCode код валюты в которую выполняется конвертация.
     * @return скновертированная сумма.
     */
    public final BigDecimal convert(final String amountString,
                                    final String toCurrencyCode,
                                    final String fromCurrencyCode) {
        return convert(parseBigDecimal(amountString),
                Currency.getInstance(toCurrencyCode),
                Currency.getInstance(fromCurrencyCode))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Конвертация из валюты в валюту.
     * @param amount сумма.
     * @param toCurrency валюта из которой выполняется конвертация.
     * @param fromCurrency валюта в которую выполняется конвертация.
     * @return скновертированная сумма.
     */
    public final BigDecimal convert(final BigDecimal amount,
                                    final Currency toCurrency,
                                    final Currency fromCurrency) {
        if (toCurrency.equals(fromCurrency)) {
            return amount;
        }
        return toBase(fromBase(checkRange(amount), fromCurrency), toCurrency)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Получение числа из строки.
     * @param amountString строка.
     * @return число.
     */
    private BigDecimal parseBigDecimal(final String amountString) {
        return checkRange(new BigDecimal(amountString));
    }

    /**
     * Проверка формата числа.
     * @param amount число.
     * @return число.
     */
    public final BigDecimal checkRange(final BigDecimal amount) {
        if (amount.stripTrailingZeros().scale() > SCALE
                || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NumberFormatException();
        }
        return amount;
    }

    /**
     * Класс курса валюты.
     */
    private static class Rate {
        /**
         * Курс перевода в базовую валюту.
         */
        private final BigDecimal to;
        /**
         * Курс перевода из базовой валюты.
         */
        private final BigDecimal from;

        /**
         * @param pTo курс перевода в базовую валюту.
         * @param pFrom курс перевода из базовой валюты.
         */
        Rate(final BigDecimal pTo,
             final BigDecimal pFrom) {
            to = pTo;
            from = pFrom;
        }
    }
}
