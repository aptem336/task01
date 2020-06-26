package lt.vu.mif.jate.task01.bank;

import java.util.Locale;
import java.util.Objects;

/**
 * Класс банка.
 */
public class Bank {
    /**
     * Страна.
     */
    private String country;
    /**
     * Код.
     */
    private Integer code;
    /**
     * Бит-код.
     */
    private String bicCode;
    /**
     * Название.
     */
    private String name;
    /**
     * Адрес.
     */
    private String address;

    /**
     * @param pCountry страна.
     * @param pCode    код.
     * @param pBitCode бит-код.
     * @param pName    название.
     * @param pAddress адрес.
     */
    public Bank(final String pCountry,
                final Integer pCode,
                final String pBitCode,
                final String pName,
                final String pAddress) {
        this(pCountry, pCode, pBitCode, pName);
        address = pAddress;
    }

    /**
     * @param pCountry страна.
     * @param pCode    код.
     * @param pBitCode бит-код.
     * @param pName    название.
     */
    public Bank(final String pCountry,
                final Integer pCode,
                final String pBitCode,
                final String pName) {
        this(pCountry, pCode, pBitCode);
        name = pName;
    }

    /**
     * @param pCountry страна.
     * @param pCode    код.
     * @param bitCode  бит-код.
     */
    public Bank(final String pCountry,
                final Integer pCode,
                final String bitCode) {
        this(pCountry, pCode);
        bicCode = bitCode;
    }

    /**
     * @param pCountry страна.
     * @param pCode    код.
     */
    public Bank(final String pCountry,
                final Integer pCode) {
        country = pCountry;
        code = pCode;
    }

    /**
     * @return локализация.
     */
    public final Locale getLocale() {
        return new Locale("en", country);
    }

    /**
     * @return код.
     */
    public final Integer getCode() {
        return code;
    }

    /**
     * @return бит-код.
     */
    public final String getBicCode() {
        return bicCode;
    }

    /**
     * @return название.
     */
    public final String getName() {
        return name;
    }

    /**
     * @return адрес.
     */
    public final String getAddress() {
        return address;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bank bank = (Bank) o;
        return Objects.equals(getLocale(), bank.getLocale())
                && Objects.equals(getCode(), bank.getCode());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getLocale(), getCode());
    }

    @Override
    public final String toString() {
        if (getName() != null) {
            return getName();
        }
        String bitCodeFormat = "";
        if (getBicCode() != null) {
            bitCodeFormat = String.format(" (%s)",
                    getBicCode());
        }
        return String.format("Bank#%d%s, %s",
                getCode(), bitCodeFormat,
                getLocale().getDisplayCountry());
    }
}
