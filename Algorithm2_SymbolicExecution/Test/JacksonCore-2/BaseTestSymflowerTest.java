package com.fasterxml.jackson.test;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class BaseTestSymflowerTest {
	@Test
	public void fiveMinuteUserFiveMinuteUser515() {
		BaseTest.FiveMinuteUser expected = new BaseTest.FiveMinuteUser();
		BaseTest.FiveMinuteUser actual = new BaseTest.FiveMinuteUser();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void fiveMinuteUserFiveMinuteUser516() {
		String first = "";
		String last = "";
		boolean verified = false;
		BaseTest.FiveMinuteUser.Gender g = BaseTest.FiveMinuteUser.Gender.FEMALE;
		byte[] data = null;
		BaseTest.FiveMinuteUser expected = new BaseTest.FiveMinuteUser(null, null, false, BaseTest.FiveMinuteUser.Gender.FEMALE, null);
		expected.setName(new BaseTest.FiveMinuteUser.Name("", ""));
		BaseTest.FiveMinuteUser actual = new BaseTest.FiveMinuteUser(first, last, verified, g, data);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void fiveMinuteUserNameName517() {
		String f = null;
		String l = null;
		BaseTest.FiveMinuteUser.Name expected = new BaseTest.FiveMinuteUser.Name(null, null);
		BaseTest.FiveMinuteUser.Name actual = new BaseTest.FiveMinuteUser.Name(f, l);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void fiveMinuteUserNameName518() {
		BaseTest.FiveMinuteUser.Name expected = new BaseTest.FiveMinuteUser.Name();
		BaseTest.FiveMinuteUser.Name actual = new BaseTest.FiveMinuteUser.Name();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void fiveMinuteUserNameEquals519() {
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		Object o = null;
		boolean actual = n.equals(o);

		assertFalse(actual);
	}

	@Test
	public void fiveMinuteUserNameEquals520() {
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		Object o = new BaseTest.FiveMinuteUser.Name(null, null);
		boolean actual = n.equals(o);

		assertTrue(actual);
	}

	@Test
	public void fiveMinuteUserNameGetFirst521() {
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		String actual = n.getFirst();

		assertNull(actual);
	}

	@Test
	public void fiveMinuteUserNameGetLast522() {
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		String actual = n.getLast();

		assertNull(actual);
	}

	@Test
	public void fiveMinuteUserNameSetFirst523() {
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		String s = null;
		n.setFirst(s);

		BaseTest.FiveMinuteUser.Name nExpected = new BaseTest.FiveMinuteUser.Name(null, null);

		assertTrue(EqualsBuilder.reflectionEquals(nExpected, n, false, null, true));
	}

	@Test
	public void fiveMinuteUserNameSetLast524() {
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		String s = null;
		n.setLast(s);

		BaseTest.FiveMinuteUser.Name nExpected = new BaseTest.FiveMinuteUser.Name(null, null);

		assertTrue(EqualsBuilder.reflectionEquals(nExpected, n, false, null, true));
	}

	@Test
	public void fiveMinuteUserEquals525() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		Object o = null;
		boolean actual = f.equals(o);

		assertFalse(actual);
	}

	@Test
	public void fiveMinuteUserEquals526() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		Object o = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		o.setName(null);
		boolean actual = f.equals(o);

		assertTrue(actual);
	}

	@Test
	public void fiveMinuteUserGetGender527() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		BaseTest.FiveMinuteUser.Gender actual = f.getGender();

		assertNull(actual);
	}

	@Test
	public void fiveMinuteUserGetName528() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		BaseTest.FiveMinuteUser.Name actual = f.getName();

		assertNull(actual);
	}

	@Test
	public void fiveMinuteUserGetUserImage529() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		byte[] actual = f.getUserImage();

		assertNull(actual);
	}

	@Test
	public void fiveMinuteUserIsVerified530() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		boolean actual = f.isVerified();

		assertFalse(actual);
	}

	@Test
	public void fiveMinuteUserSetGender531() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		BaseTest.FiveMinuteUser.Gender g = BaseTest.FiveMinuteUser.Gender.FEMALE;
		f.setGender(g);

		BaseTest.FiveMinuteUser fExpected = new BaseTest.FiveMinuteUser(null, null, false, BaseTest.FiveMinuteUser.Gender.FEMALE, null);
		fExpected.setName(null);

		assertTrue(EqualsBuilder.reflectionEquals(fExpected, f, false, null, true));
	}

	@Test
	public void fiveMinuteUserSetName532() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		BaseTest.FiveMinuteUser.Name n = new BaseTest.FiveMinuteUser.Name(null, null);
		f.setName(n);

		BaseTest.FiveMinuteUser fExpected = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		fExpected.setName(new BaseTest.FiveMinuteUser.Name(null, null));

		assertTrue(EqualsBuilder.reflectionEquals(fExpected, f, false, null, true));
	}

	@Test
	public void fiveMinuteUserSetUserImage533() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		byte[] b = null;
		f.setUserImage(b);

		BaseTest.FiveMinuteUser fExpected = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		fExpected.setName(null);

		assertTrue(EqualsBuilder.reflectionEquals(fExpected, f, false, null, true));
	}

	@Test
	public void fiveMinuteUserSetVerified534() {
		BaseTest.FiveMinuteUser f = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		f.setName(null);
		boolean b = false;
		f.setVerified(b);

		BaseTest.FiveMinuteUser fExpected = new BaseTest.FiveMinuteUser(null, null, false, null, null);
		fExpected.setName(null);

		assertTrue(EqualsBuilder.reflectionEquals(fExpected, f, false, null, true));
	}
}
