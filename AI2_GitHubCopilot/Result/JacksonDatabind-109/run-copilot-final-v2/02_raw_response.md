package com.fasterxml.jackson.databind.ser.std;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class NumberSerializerBigDecimalPlainTest {

 private ObjectMapper mapperPlain;
 private ObjectMapper mapperDefault;

 @Before
 public void setUp() {
     mapperPlain = new ObjectMapper();
     mapperPlain.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
     mapperDefault = new ObjectMapper();
     mapperDefault.disable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
 }

 @Test
 public void testPlainEnabledNormalValues() throws Exception {
     assertEquals("0.1", mapperPlain.writeValueAsString(new BigDecimal("0.1")));
     assertEquals("100", mapperPlain.writeValueAsString(new BigDecimal("100")));
 }

 @Test
 public void testPlainEnabledBoundaryValues() throws Exception {
     assertEquals("0.0000000005", mapperPlain.writeValueAsString(new BigDecimal("0.0000000005")));
     assertEquals("0.00000000000000000001", mapperPlain.writeValueAsString(new
BigDecimal("1E-20")));
     assertEquals("-0.00000000000000000001", mapperPlain.writeValueAsString(new
BigDecimal("-1E-20")));
 }

 @Test
 public void testPlainEnabledLargeIntegerScale() throws Exception {
     assertEquals("100000000000000000000", mapperPlain.writeValueAsString(new BigDecimal("1E+20")));
     assertEquals("0", mapperPlain.writeValueAsString(BigDecimal.ZERO));
 }

 @Test
 public void testPlainDisabledExponentOutput() throws Exception {
     assertEquals("5E-10", mapperDefault.writeValueAsString(new BigDecimal("0.0000000005")));
     assertEquals("1E-20", mapperDefault.writeValueAsString(new BigDecimal("1E-20")));
     assertEquals("1E+20", mapperDefault.writeValueAsString(new BigDecimal("1E+20")));
 }

 @Test
 public void testBigIntegerUnaffectedByFeature() throws Exception {
     BigInteger bigInt = new BigInteger("123456789012345678901234567890");
     assertEquals(bigInt.toString(), mapperPlain.writeValueAsString(bigInt));
     assertEquals(bigInt.toString(), mapperDefault.writeValueAsString(bigInt));
 }

 @Test
 public void testNonBigDecimalNumberTypes() throws Exception {
     assertEquals("123", mapperPlain.writeValueAsString(Integer.valueOf(123)));
     assertEquals("123.45", mapperPlain.writeValueAsString(Double.valueOf(123.45)));
     assertEquals("1234567890123456789",
mapperPlain.writeValueAsString(Long.valueOf(1234567890123456789L)));
     assertEquals("1.5", mapperPlain.writeValueAsString(Float.valueOf(1.5f)));
 }

 @Test
 public void testNullNumber() throws Exception {
     String json = mapperPlain.writeValueAsString(null);
     assertEquals("null", json);
 }

 @Test
 public void testPlainKeepsTrailingZeros() throws Exception {
     assertEquals("0.00", mapperPlain.writeValueAsString(new BigDecimal("0.00")));
     assertEquals("1.0", mapperPlain.writeValueAsString(new BigDecimal("1.0")));

 }
}
```