package org.apache.commons.lang3.time;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class StopWatchSymflowerTest {
	@Test
	public void StopWatch19() throws IllegalAccessException, NoSuchFieldException {
		StopWatch expected = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(expected, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(expected, 10);
		StopWatch actual = new StopWatch();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void getNanoTime20() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		long expected = 0L;
		long actual = s.getNanoTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getNanoTime21() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 2);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, -9223372036854775808L);
		long expected = -9223372036854775808L;
		long actual = s.getNanoTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getNanoTime22() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 2);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, 1077936128L);
		long expected = -1077936128L;
		long actual = s.getNanoTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getNanoTime23() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 3);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, -9223372036854775680L);
		final Field fieldStopTime = StopWatch.class.getDeclaredField("stopTime");
		fieldStopTime.setAccessible(true);
		fieldStopTime.set(s, -9223372036854775808L);
		long expected = -128L;
		long actual = s.getNanoTime();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.RuntimeException.class)
	public void getNanoTime24() throws IllegalAccessException, NoSuchFieldException, RuntimeException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 4);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.getNanoTime();
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void getSplitNanoTime25() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.getSplitNanoTime();
	}

	@Test
	public void getSplitNanoTime26() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 11);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, -9223372036854775808L);
		long expected = -9223372036854775808L;
		long actual = s.getSplitNanoTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getSplitNanoTime27() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 11);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, 1077936128L);
		long expected = -1077936128L;
		long actual = s.getSplitNanoTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getSplitTime28() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 11);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, -9223372036854775808L);
		long expected = -9223372036854L;
		long actual = s.getSplitTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getSplitTime29() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 11);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, 1L);
		long expected = 0L;
		long actual = s.getSplitTime();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void getStartTime30() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.getStartTime();
	}

	@Test
	public void getStartTime31() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 1);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		long expected = 0L;
		long actual = s.getStartTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getTime32() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 2);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, -9223372036854775808L);
		long expected = -9223372036854L;
		long actual = s.getTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getTime33() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 2);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, 1L);
		long expected = 0L;
		long actual = s.getTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getTime34() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 3);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		final Field fieldStartTime = StopWatch.class.getDeclaredField("startTime");
		fieldStartTime.setAccessible(true);
		fieldStartTime.set(s, 1L);
		long expected = 0L;
		long actual = s.getTime();

		assertEquals(expected, actual);
	}

	@Test
	public void reset35() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.reset();

		StopWatch sExpected = new StopWatch();
		final Field fieldRunningState2 = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState2.setAccessible(true);
		fieldRunningState2.set(sExpected, 0);
		final Field fieldSplitState2 = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState2.setAccessible(true);
		fieldSplitState2.set(sExpected, 10);

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void resume36() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.resume();
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void split37() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.split();
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void start38() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 1);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.start();
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void start39() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 2);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.start();
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void stop40() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.stop();
	}

	@Test
	public void stop41() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 3);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.stop();

		StopWatch sExpected = new StopWatch();
		final Field fieldRunningState2 = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState2.setAccessible(true);
		fieldRunningState2.set(sExpected, 2);
		final Field fieldSplitState2 = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState2.setAccessible(true);
		fieldSplitState2.set(sExpected, 0);

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void suspend42() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.suspend();
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void unsplit43() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 0);
		s.unsplit();
	}

	@Test
	public void unsplit44() throws IllegalAccessException, NoSuchFieldException {
		StopWatch s = new StopWatch();
		final Field fieldRunningState = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState.setAccessible(true);
		fieldRunningState.set(s, 0);
		final Field fieldSplitState = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState.setAccessible(true);
		fieldSplitState.set(s, 11);
		s.unsplit();

		StopWatch sExpected = new StopWatch();
		final Field fieldRunningState2 = StopWatch.class.getDeclaredField("runningState");
		fieldRunningState2.setAccessible(true);
		fieldRunningState2.set(sExpected, 0);
		final Field fieldSplitState2 = StopWatch.class.getDeclaredField("splitState");
		fieldSplitState2.setAccessible(true);
		fieldSplitState2.set(sExpected, 10);

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}
}
