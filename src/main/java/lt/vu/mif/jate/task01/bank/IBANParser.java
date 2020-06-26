package lt.vu.mif.jate.task01.bank;

import lt.vu.mif.jate.task01.bank.exception.BankNotFoundException;
import lt.vu.mif.jate.task01.bank.exception.IBANException;

import java.math.BigInteger;

/**
 * Парсер международного кода.
 */
public class IBANParser {
    /**
     * Символ кода банка.
     */
    private static final char BANK_CHAR = 'b';
    /**
     * Символ кода счёта.
     */
    private static final char NUMBER_CHAR = 'c';
    /**
     * Страна.
     */
    private final String country;
    /**
     * Паттерн международного кода.
     */
    private final String patternString;

    /**
     * @param pCountry страна.
     * @param pPatternString паттерн международного кода.
     */
    public IBANParser(final String pCountry,
                      final String pPatternString) {
        country = pCountry;
        patternString = pPatternString;
    }

    /**
     * Метод получения банка из междурадного кода.
     * @param iban международнй код.
     * @return банк.
     * @throws IBANException ошибка формата кода.
     */
    public final Bank parseBank(final String iban)
            throws IBANException {
        int code = Integer.parseInt(parseByChar(BANK_CHAR, iban));
        Bank bank = null;
        Banking banking = Banking.getInstance();
        try {
            bank = banking.getBank(country, code, true);
        } catch (BankNotFoundException ignored) {
        }
        return bank;
    }

    /**
     * Метод полученя номера счёта.
     * @param iban международный код.
     * @return номер счта.
     * @throws IBANException ошибка формата кода.
     */
    public final BigInteger parseNumber(final String iban)
            throws IBANException {
        try {
            return new BigInteger(parseByChar(NUMBER_CHAR, iban));
        } catch (NumberFormatException ex) {
            throw new IBANException(iban);
        }
    }

    /**
     * Метод получения строки по маске символа.
     * @param c символ.
     * @param iban международный код.
     * @return полученная строка.
     * @throws IBANException ошибка формата кода.
     */
    private String parseByChar(final char c, final String iban)
            throws IBANException {
        if (iban.length() != patternString.length()) {
            throw new IBANException(patternString.length(),
                    iban.length(), iban);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < patternString.length(); i++) {
            if (patternString.charAt(i) == c) {
                result.append(iban.charAt(i));
            }
        }
        return result.toString();
    }
}
