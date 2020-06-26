package lt.vu.mif.jate.task01.bank.exception;

import lt.vu.mif.jate.task01.bank.Account;

/**
 * НИсключение неверный тип аккаунта.
 */
public class WrongAccountTypeException extends Exception {
    /**
     * Формат сообщения об ошибке.
     */
    private static final String ACCOUNT_TYPE_WAS_MESSAGE
            = "Account type was %s";
    /**
     * Сообщения об ошибке.
     */
    private final String message;

    /**
     * @param accountClass тип аккаунта.
     */
    public WrongAccountTypeException(
            final Class<? extends Account> accountClass) {
        message = String.format(ACCOUNT_TYPE_WAS_MESSAGE,
                accountClass.getSimpleName());
    }

    @Override
    public final String getMessage() {
        return message;
    }
}
