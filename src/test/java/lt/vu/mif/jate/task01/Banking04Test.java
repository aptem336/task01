package lt.vu.mif.jate.task01;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.Set;
import java.util.function.Consumer;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import lt.vu.mif.jate.task01.bank.Converter;
import lt.vu.mif.jate.task01.bank.Banking;

/**
 * Create Converter class that would test.
 * Converter is a currency converter utility that
 * uses EUR as the base currency. Currency rates must
 * be loaded from resource /banking/rates.txt.
 * Conversion is performed to base currency and then
 * to the target currency.
 * @author valdo
 */
@RunWith(JUnit4.class)
public class Banking04Test implements BankingTestIf {
        
    private final Banking banking = Banking.getInstance();

    @Test
    public void converterTest() {

        Converter conv = banking.getConverter();
        
        // Simple tests
        
        Currency base = conv.getBaseCurrency();
        assertEquals("EUR", base.getCurrencyCode());

        assertEquals(BD("0.6338"), conv.getRateToBase("AUD"));
        assertEquals(BD("1.5797"), conv.getRateFromBase("AUD"));
        
        assertEquals(BD("0.63"), conv.toBase("1.00", "AUD"));
        assertEquals(BD("1.58"), conv.fromBase("1.00", "AUD"));
        assertEquals(BD("0.63"), conv.convert("1.00", "AUD", "EUR"));
        assertEquals(BD("7.04"), conv.convert("11.11", "AUD", "EUR"));
        assertEquals(BD("1.58"), conv.convert("1.00", "EUR", "AUD"));
        assertEquals(BD("19.13"), conv.convert("12.11", "EUR", "AUD"));
        
        assertEquals(BD("1.3007"), conv.getRateToBase("GBP"));
        assertEquals(BD("0.7698"), conv.getRateFromBase("GBP"));

        assertEquals(BD("1.30"), conv.toBase("1.00", "GBP"));
        assertEquals(BD("0.77"), conv.fromBase("1.00", "GBP"));
        assertEquals(BD("1.30"), conv.convert("1.00", "GBP", "EUR"));
        assertEquals(BD("0.77"), conv.convert("1.00", "EUR", "GBP"));
        assertEquals(BD("130.07"), conv.convert("100.00", "GBP", "EUR"));
        assertEquals(BD("100.13"), conv.convert("130.07", "EUR", "GBP"));
        
        assertEquals(BD("1"), conv.getRateToBase(base));
        assertEquals(BD("1"), conv.getRateFromBase(base));
        
        assertEquals(BD("1.00"), conv.convert("1.00", "EUR", "EUR"));
        assertEquals(BD("1.00"), conv.convert("1.00", "EUR", "EUR"));
        assertEquals(BD("100.00"), conv.convert("100.00", "EUR", "EUR"));
        assertEquals(BD("77.78"), conv.convert("77.78", "EUR", "EUR"));
        
        assertEquals(BD("0.49"), conv.convert("1.00", "AUD", "GBP"));
        assertEquals(BD("2.05"), conv.convert("1.00", "GBP", "AUD"));
        assertEquals(BD("30.79"), conv.convert("1000000.00", "BYR", "GBP"));
        assertEquals(BD("161843.03"), conv.convert("1234567.89", "XAF", "RUB"));
        
        Set<Currency> currencies = conv.getCurrencies();
        assertEquals(189, currencies.size());

        // Testing everything with everything with random ammounts
        
        currencies.stream().forEach(cfrom -> { 
            currencies.stream().forEach(cto -> {
                BigDecimal toBase = conv.getRateToBase(cfrom);
                BigDecimal fromBase = conv.getRateFromBase(cto);
                BigDecimal ammount = new BigDecimal(Math.random()).setScale(2, BigDecimal.ROUND_HALF_UP);
                
                BigDecimal result = ammount;
                if (!cfrom.equals(cto)) {
                    result = ammount
                        .multiply(toBase)
                        .multiply(fromBase)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                
                assertEquals(result, conv.convert(ammount, cfrom, cto));
                
            }); 
        });

        // Check wrong ammounts are rejected with the right exception
        
        Arrays.asList("1.001", "-100", "-0.01", "1,00", "12.a", "Labas").stream().forEach((s) -> {

            checkException(s, v -> conv.toBase(v, "GBP"), NumberFormatException.class);
            checkException(s, v -> conv.toBase(v, "G12"), NumberFormatException.class);
            checkException(s, v -> conv.fromBase(v, "GBP"), NumberFormatException.class);
            checkException(s, v -> conv.fromBase(v, "G32"), NumberFormatException.class);
            checkException(s, v -> conv.convert(v, "GBP", "EUR"), NumberFormatException.class);
            checkException(s, v -> conv.convert(v, "G32", "EUR"), NumberFormatException.class);
            checkException(s, v -> conv.convert(v, "GBP", "E32"), NumberFormatException.class);
            checkException(s, 
                v -> conv.convert(new BigDecimal(v), 
                        Currency.getInstance("GBP"), 
                        Currency.getInstance("EUR")),
                NumberFormatException.class);
            
        });
        
        // Check wrong currencies are rejected with the right exception
        
        Arrays.asList("gbpa", "10", "eu", "US", "EWR").stream().forEach((s) -> {
            
            checkException(s, v -> conv.toBase("1", v), IllegalArgumentException.class);
            checkException(s, v -> conv.fromBase("1", v), IllegalArgumentException.class);
            checkException(s, v -> conv.convert("1", v, "GBP"), IllegalArgumentException.class);
            checkException(s, v -> conv.convert("1", "GBP", v), IllegalArgumentException.class);
            checkException(s, v -> conv.convert("1", v, v), IllegalArgumentException.class);
            
        });
        
    }
    
}
