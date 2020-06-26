package lt.vu.mif.jate.task01;

import java.math.BigDecimal;
import java.util.function.Consumer;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * Base for all test classes.
 * @author valdo
 */
public interface BankingTestIf {
    
    /**
     * Convert string to BigDecimal.
     * @param value value to be converted.
     * @return converted value.
     */
    default BigDecimal BD(String value) { 
        return new BigDecimal(value);
    }

    /**
     * Check that required exception is being thrown with supplied parameter.
     * @param <V> parameter type to be provided.
     * @param <E> exception type to be checked.
     * @param value actual parameter value.
     * @param op consumer operation to execute.
     * @param exception exception class to expect.
     */
    default <V, E extends Throwable> void checkException(V value, Consumer<V> op, Class<E> exception) {
        try {
            op.accept(value);
            fail(String.format("Should have failed with %s: %s", exception.getSimpleName(), value.toString()));
        } catch (Throwable ex) {
            assertEquals(exception, ex.getClass());
        }
    }
    
}
