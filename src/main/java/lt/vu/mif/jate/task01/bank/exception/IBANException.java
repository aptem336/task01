package lt.vu.mif.jate.task01.bank.exception;

/**
 * Иксключение формата междунарожного кода счета.
 */
public class IBANException extends Exception {
    /**
     * Формат сообщения банк не найден.
     */
    private static final String COUNTRY_NOT_FOUND_MESSAGE
            = "IBAN country not found: %s";
    /**
     * Формат сообщения неверная длина.
     */
    private static final String NUMBER_LENGTH_WRONG_MESSAGE
            = "IBAN number length wrong: expected %d, got %d";
    /**
     * Формат сообщения неверный формат.
     */
    private static final String FORMAT_WRONG_MESSAGE
            = "IBAN format wrong: %s";
    /**
     * Сообщения.
     */
    private final String message;
    /**
     * Международный код.
     */
    private final String value;

    /**
     * @param country страна.
     * @param pValue  международный код.
     */
    public IBANException(final String country,
                         final String pValue) {
        message = String.format(COUNTRY_NOT_FOUND_MESSAGE, country);
        value = pValue;
    }

    /**
     * @param expected ожидаемая длина.
     * @param got      полученная длина.
     * @param pValue   международный код.
     */
    public IBANException(final int expected,
                         final int got,
                         final String pValue) {
        message = String.format(NUMBER_LENGTH_WRONG_MESSAGE,
                expected, got);
        value = pValue;
    }

    /**
     * @param pValue международный код.
     */
    public IBANException(final String pValue) {
        message = String.format(FORMAT_WRONG_MESSAGE, pValue);
        value = pValue;
    }

    /**
     * @return международный код.
     */
    public final String getValue() {
        return value;
    }

    @Override
    public final String getMessage() {
        return message;
    }
}
