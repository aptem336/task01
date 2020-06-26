package lt.vu.mif.jate.task01.bank;

import lt.vu.mif.jate.task01.bank.exception.BankNotFoundException;
import lt.vu.mif.jate.task01.bank.exception.IBANException;
import lt.vu.mif.jate.task01.bank.exception.WrongAccountTypeException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Основной клас финансовой бизнес-логики.
 */
public class Banking {
    /**
     * Путь к ресурсам.
     */
    public static final String RESOURCES_PATH = "src/test/resources/banking/";
    /**
     * Индекс названия.
     */
    private static final int NAME_INDEX = 0;
    /**
     * Индекс адрес.
     */
    private static final int ADDRESS_INDEX = 1;
    /**
     * Индекс бит-кода.
     */
    private static final int BIT_CODE_INDEX = 2;
    /**
     * Индекс кода.
     */
    private static final int CODE_INDEX = 3;
    /**
     * Синглтон банка.
     */
    private static Banking instance;
    /**
     * Банки стран.
     */
    private Map<String, Map<Integer, Bank>> banks;
    /**
     * Парсер международных кодов счета.
     */
    private Map<String, IBANParser> ibanParsers;
    /**
     * Банковские счета.
     */
    private Map<String, Account> accounts;

    {
        try (final BufferedReader banksBufferedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(RESOURCES_PATH
                                + "banks.txt"),
                        StandardCharsets.UTF_8));
             final BufferedReader ibanBufferedReader = new BufferedReader(
                     new InputStreamReader(
                             new FileInputStream(RESOURCES_PATH
                                     + "iban.txt"),
                             StandardCharsets.UTF_8))) {
            banks = readBanks(banksBufferedReader);
            ibanParsers = readIban(ibanBufferedReader);
        } catch (IOException ignored) {
        }
    }

    /**
     * @return объект текущего класса.
     */
    public static synchronized Banking getInstance() {
        if (instance == null) {
            instance = new Banking();
        }
        return instance;
    }

    /**
     * Метод чтения банков.
     *
     * @param banksBufferedReader ридер файла.
     * @return прочтенные банки Латвии.
     * @throws IOException ошибка чтения.
     */
    private static Map<String, Map<Integer, Bank>> readBanks(
            final BufferedReader banksBufferedReader)
            throws IOException {
        Map<String, Map<Integer, Bank>> banks = new HashMap<>();

        Map<Integer, Bank> ltBanks = new HashMap<>();
        banks.put("LT", ltBanks);

        String bankLine;
        while ((bankLine = banksBufferedReader.readLine()) != null) {
            String[] bankAttributes = bankLine.split(":");
            Integer code = Integer.valueOf(bankAttributes[CODE_INDEX]);
            String bicCode = bankAttributes[BIT_CODE_INDEX];
            String name = bankAttributes[NAME_INDEX];
            String address = bankAttributes[ADDRESS_INDEX];
            ltBanks.put(code,
                    new Bank("LT", code, bicCode, name, address));
        }
        return banks;
    }

    /**
     * Метод чтения банковских счетов.
     *
     * @param ibanBufferedReader ридер файла.
     * @return банкоский счета.
     * @throws IOException ошибка чтения.
     */
    private static Map<String, IBANParser> readIban(
            final BufferedReader ibanBufferedReader)
            throws IOException {
        Map<String, IBANParser> ibanParsers = new HashMap<>();
        String patternLine;
        while ((patternLine = ibanBufferedReader.readLine()) != null) {
            String[] ibanAttributes = patternLine.split(":");
            String ibanString = ibanAttributes[1];
            String country = ibanString.substring(0, 2);
            ibanParsers.put(country,
                    new IBANParser(country,
                            ibanString.replace(" ", "")));
        }
        return ibanParsers;
    }

    /**
     * @return банки стран.
     */
    public final Map<String, Map<Integer, Bank>> getBanks() {
        Map<String, Map<Integer, Bank>> clone = new HashMap<>();
        banks.forEach((key, value) -> clone.put(key, new HashMap<>(value)));
        return clone;
    }

    /**
     * Банк по стране и коду.
     *
     * @param country страна.
     * @param code    код.
     * @return найденный банк.
     * @throws BankNotFoundException банк не ннайден.
     */
    public final Bank getBank(final String country,
                              final int code)
            throws BankNotFoundException {
        return getBank(country, code, false);
    }

    /**
     * @param country страна.
     * @param code    код.
     * @param produce нужно-ли создавать банк, если он не найден.
     * @return банк по старне и коду.
     * @throws BankNotFoundException банк не найден.
     */
    public final Bank getBank(final String country,
                              final int code,
                              final boolean produce)
            throws BankNotFoundException {
        Map<Integer, Bank> countryBanks = banks.get(country);
        if (countryBanks == null) {
            if (produce) {
                countryBanks = new HashMap<>();
                banks.put(country, countryBanks);
            } else {
                throw new BankNotFoundException(country, code);
            }
        }
        Bank bank = countryBanks.get(code);
        if (bank == null) {
            if (produce) {
                bank = new Bank(country, code);
                countryBanks.put(code, bank);
            } else {
                throw new BankNotFoundException(country, code);
            }
        }
        return bank;
    }

    /**
     * @return банкоские счета.
     */
    public final Map<String, Account> getAccounts() {
        if (accounts == null) {
            accounts = new HashMap<>();
        }
        return accounts;
    }

    /**
     * Счёт по международному коду.
     *
     * @param iban         междунарожный счёт.
     * @param accountClass тип счёта.
     * @return счёт соответствующий коду.
     * @throws IBANException             ошибка форма кода.
     * @throws WrongAccountTypeException неверный тип счёта.
     */
    public final Account getAccount(final String iban,
                              final Class<? extends Account> accountClass)
            throws IBANException, WrongAccountTypeException {
        String country = iban.substring(0, 2);
        IBANParser ibanPattern = ibanParsers.get(country);
        if (ibanPattern == null) {
            throw new IBANException(country, iban);
        }
        Bank bank = ibanPattern.parseBank(iban);
        BigInteger number = ibanPattern.parseNumber(iban);

        Account account = getAccounts().get(iban);
        if (account == null) {
            try {
                account = accountClass
                        .getDeclaredConstructor(String.class,
                                Bank.class,
                                BigInteger.class)
                        .newInstance(iban,
                                bank,
                                number);
            } catch (InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            getAccounts().put(iban, account);
        } else if (!accountClass.isInstance(account)) {
            throw new WrongAccountTypeException(account.getClass());
        }
        return account;
    }

    /**
     * Получить лицевой счёт.
     *
     * @param iban международный код.
     * @return лицевой счёт.
     * @throws IBANException             ошибка формата международного кода.
     * @throws WrongAccountTypeException неверный тип счёта.
     */
    public final CurrentAccount getCurrentAccount(final String iban)
            throws IBANException, WrongAccountTypeException {
        return (CurrentAccount) getAccount(formatIban(iban),
                CurrentAccount.class);
    }

    /**
     * Получить кредитный счёт.
     *
     * @param iban международный код.
     * @return кредитный счёт.
     * @throws IBANException             ошибка формата международного кода.
     * @throws WrongAccountTypeException неверный тип счёта.
     */
    public final CreditAccount getCreditAccount(final String iban)
            throws IBANException, WrongAccountTypeException {
        return (CreditAccount) getAccount(formatIban(iban),
                CreditAccount.class);
    }

    /**
     * Получить сберегательный счёт.
     *
     * @param iban международный код.
     * @return сберегательный счёт.
     * @throws IBANException             ошибка формата международного кода.
     * @throws WrongAccountTypeException неверный тип счёта.
     */
    public final SavingsAccount getSavingsAccount(final String iban)
            throws IBANException, WrongAccountTypeException {
        return (SavingsAccount) getAccount(formatIban(iban),
                SavingsAccount.class);
    }

    /**
     * Метод предварительной обработки международного кода счёта.
     *
     * @param iban междунарожный код.
     * @return обработанный междунарожный код.
     */
    private String formatIban(final String iban) {
        return iban.replaceAll(" ", "")
                .toUpperCase(Locale.getDefault());
    }

    /**
     * @return объект конвертера.
     */
    public final Converter getConverter() {
        return Converter.getInstance();
    }
}
