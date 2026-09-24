package org.apache.commons.lang3.math;
import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class NumberUtilsNSGA2Test {
    @Test public void input_01() {
        Number actual = NumberUtils.createNumber("0x1");
        assertEquals(new BigInteger("1"),
            new BigInteger(actual.toString()));
    }

    @Test public void input_05() {
        Number actual = NumberUtils.createNumber("0xFFFFFFFF");
        assertEquals(new BigInteger("4294967295"),
            new BigInteger(actual.toString()));
    }

    @Test public void input_09() {
        Number actual = NumberUtils.createNumber("0x10000000000000000");
        assertEquals(new BigInteger("18446744073709551616"),
            new BigInteger(actual.toString()));
    }

    @Test public void input_14() {
        Number actual = NumberUtils.createNumber("#80000000");
        assertEquals(new BigInteger("2147483648"),
            new BigInteger(actual.toString()));
    }
}
