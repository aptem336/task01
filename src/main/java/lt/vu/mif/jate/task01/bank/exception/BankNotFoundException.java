package lt.vu.mif.jate.task01.bank.exception;

/**
 * Исключение банк не найден.
 */
public class BankNotFoundException extends Exception {
    /**
     * Формат сообщения об ошибке.
     */
    private static final String BANK_WAS_NOT_FOUND_MESSAGE
            = "Bank (%s-%d) was not found.";
    /**
     * Сообщение.
     */
    private final String message;
    /**
     * Страна.
     */
    private final String country;
    /**
     * Код.
     */
    private final Integer code;

    /**
     * @param pCountry страна.
     * @param pCode код.
     */
    public BankNotFoundException(final String pCountry,
                                 final int pCode) {
        message = String.format(BANK_WAS_NOT_FOUND_MESSAGE, pCountry, pCode);
        country = pCountry;
        code = pCode;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    /**
     * @return страна.
     */
    public final String getCountry() {
        return country;
    }

    /**
     * @return код.
     */
    public final Integer getCode() {
        return code;
    }
}
