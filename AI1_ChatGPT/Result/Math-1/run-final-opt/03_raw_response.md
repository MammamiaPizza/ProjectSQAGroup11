import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

public class FractionDigitLimitConversionTest {

    @Test
    public void bigFractionHalfWithSmallDigitLimitsIsReducedHalf() {
        BigFraction withLimitOne = new BigFraction(0.5, 1);
        assertEquals(BigInteger.ONE, withLimitOne.getNumerator());
        assertEquals(BigInteger.valueOf(2), withLimitOne.getDenominator());

        BigFraction withLimitTwo = new BigFraction(0.5, 2);
        assertEquals(BigInteger.ONE, withLimitTwo.getNumerator());
        assertEquals(BigInteger.valueOf(2), withLimitTwo.getDenominator());
    }

    @Test
    public void fractionHalfWithSmallDigitLimitsIsReducedHalf() {
        Fraction withLimitOne = new Fraction(0.5, 1);
        assertEquals(1, withLimitOne.getNumerator());
        assertEquals(2, withLimitOne.getDenominator());

        Fraction withLimitTwo = new Fraction(0.5, 2);
        assertEquals(1, withLimitTwo.getNumerator());
        assertEquals(2, withLimitTwo.getDenominator());
    }
}