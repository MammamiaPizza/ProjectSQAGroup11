@Test
public void testAddEmptyString() { consumer.add(""); assertEquals("", consumer.getOutput());
}

@Test
public void testAddWordAfterWord() { consumer.add("a"); consumer.add("b"); assertEquals("a b",
consumer.getOutput());
}

@Test
public void testAddNumberNegativeAfterMinus() { consumer.add("-"); consumer.add(-5.0);
assertEquals("- -5", consumer.getOutput());
}

@Test
public void testAddNumberLargeScientific() { consumer.add(1000.0); assertEquals("1E3",
consumer.getOutput());
}