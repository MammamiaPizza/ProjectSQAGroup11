@Test
public void testAddOpSeparatesRepeatedPlusOperators() {
  GapCoverageCodeConsumer consumer = new GapCoverageCodeConsumer();
  consumer.append("x+");

  consumer.addOp("+", true);

  assertEquals("x+ +", consumer.getOutput());
}

@Test
public void testAddOpSeparatesWordOperatorAfterWordCharacter() {
  GapCoverageCodeConsumer consumer = new GapCoverageCodeConsumer();
  consumer.append("x");

  consumer.addOp("instanceof", true);

  assertEquals("x instanceof", consumer.getOutput());
}

@Test
public void testAddOpAvoidsFormingHtmlCommentClose() {
  GapCoverageCodeConsumer consumer = new GapCoverageCodeConsumer();
  consumer.append("-");

  consumer.addOp(">", true);

  assertEquals("- >", consumer.getOutput());
}

@Test
public void testAddNumberUsesExponentForLargePowerOfTen() {
  GapCoverageCodeConsumer consumer = new GapCoverageCodeConsumer();

  consumer.addNumber(1000.0);

  assertEquals("1E3", consumer.getOutput());
}

private static final class GapCoverageCodeConsumer extends CodeConsumer {
  private final java.lang.StringBuilder output = new java.lang.StringBuilder();

  @Override
  char getLastChar() {
    return output.length() == 0 ? '\0' : output.charAt(output.length() - 1);
  }

  @Override
  void append(String str) {
    output.append(str);
  }

  String getOutput() {
    return output.toString();
  }
}