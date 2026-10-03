package org.apache.commons.lang3.math;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumberUtilsIsNumberTest {

 @Test
 public void isNumberValidDecimals() {
     assertTrue(NumberUtils.isNumber("123"));
     assertTrue(NumberUtils.isNumber("0"));
     assertTrue(NumberUtils.isNumber("007"));
     assertTrue(NumberUtils.isNumber("0001"));
 }

 @Test
 public void isNumberValidSignedDecimals() {
     assertTrue(NumberUtils.isNumber("-456"));
     assertTrue(NumberUtils.isNumber("-0"));
     assertTrue(NumberUtils.isNumber("-1"));
 }

 @Test
 public void isNumberValidFloatingPoint() {
     assertTrue(NumberUtils.isNumber("1.5"));
     assertTrue(NumberUtils.isNumber("0.0"));
     assertTrue(NumberUtils.isNumber(".5"));
     assertTrue(NumberUtils.isNumber("5."));
     assertTrue(NumberUtils.isNumber("-0.5"));
 }

 @Test
 public void isNumberValidScientificNotation() {
     assertTrue(NumberUtils.isNumber("1e5"));
     assertTrue(NumberUtils.isNumber("-1E-10"));
     assertTrue(NumberUtils.isNumber("6e2"));
     assertTrue(NumberUtils.isNumber("3.4e5"));
     assertTrue(NumberUtils.isNumber("1E+5"));
 }

 @Test
 public void isNumberValidHexadecimal() {
     assertTrue(NumberUtils.isNumber("0x1A"));
     assertTrue(NumberUtils.isNumber("0x0"));
     assertTrue(NumberUtils.isNumber("0xF"));
     assertTrue(NumberUtils.isNumber("0xAB12"));
 }

 @Test
 public void isNumberValidNegativeHexadecimal() {
     assertTrue(NumberUtils.isNumber("-0x1A"));
     assertTrue(NumberUtils.isNumber("-0x0"));
     assertTrue(NumberUtils.isNumber("-0xA"));
 }

 @Test
 public void isNumberValidTypeSuffixes() {
     assertTrue(NumberUtils.isNumber("1L"));
     assertTrue(NumberUtils.isNumber("1l"));
     assertTrue(NumberUtils.isNumber("1f"));
     assertTrue(NumberUtils.isNumber("1F"));
     assertTrue(NumberUtils.isNumber("1d"));
     assertTrue(NumberUtils.isNumber("1D"));
 }

 @Test
 public void isNumberNullAndBlankAreFalse() {
     assertFalse(NumberUtils.isNumber(null));
     assertFalse(NumberUtils.isNumber(""));
     assertFalse(NumberUtils.isNumber(" "));
     assertFalse(NumberUtils.isNumber("\t"));
 }

 @Test
 public void isNumberInvalidAlphanumericAreFalse() {
     assertFalse(NumberUtils.isNumber("abc"));
     assertFalse(NumberUtils.isNumber("1a"));
     assertFalse(NumberUtils.isNumber("1 2"));
 }

 @Test
 public void isNumberInvalidStructureAreFalse() {
     assertFalse(NumberUtils.isNumber("--1"));
     assertFalse(NumberUtils.isNumber("1.2.3"));
     assertFalse(NumberUtils.isNumber("1e"));
     assertFalse(NumberUtils.isNumber("1e1.1"));
 }

 @Test
 public void isNumberInvalidHexadecimalAreFalse() {
     assertFalse(NumberUtils.isNumber("0x"));
     assertFalse(NumberUtils.isNumber("0xG"));
     assertFalse(NumberUtils.isNumber("0x12G"));
 }

 @Test
 public void isNumberBoundaryLeadingAndTrailingDot() {
     assertTrue(NumberUtils.isNumber("1."));
     assertTrue(NumberUtils.isNumber(".5"));
     assertTrue(NumberUtils.isNumber("5."));
     assertTrue(NumberUtils.isNumber("0001"));
 }

}